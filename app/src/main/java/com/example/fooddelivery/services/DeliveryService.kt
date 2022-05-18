package com.example.fooddelivery.services

import android.annotation.SuppressLint
import android.app.*
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fooddelivery.MainActivity
import com.example.fooddelivery.R
import com.example.fooddelivery.data.Delivery
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeliveryService : IntentService("DeliveryIntentService") {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var deliveryDescription: String
    private var serviceDelivery: Delivery? = null

    override fun onHandleIntent(intent: Intent?) {
        deliveryDescription = intent!!.extras!!.get("deliveryName") as String

        addDeliveryListener()
        while(serviceDelivery == null){

        }

        var i = 0
        while (i < 100) {
            Thread.sleep(1000)
            i += 5
            serviceDelivery!!.progress = i
            if(i == 100){
                serviceDelivery!!.completed = true
            }
            firebaseDatabase.reference.child("delivery_table").child(serviceDelivery!!.id).setValue(serviceDelivery)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "deliveryChannel", "delivery",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_NO_CREATE)
            }
        }
        val notification = NotificationCompat.Builder(this, "deliveryChannel")
            .setContentTitle("Your order is delivered!")
            .setContentText("Please confirm receipt")
            .setSmallIcon(R.drawable.ic_baseline_moped_24)
            .setContentIntent(pendingIntent)
            .build()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notification)

            //stopService(Intent(this, DeliveryService::class.java))
    }

    private fun addDeliveryListener() {
        val valueEventListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val delivery = child.getValue(Delivery::class.java)
                    if (delivery != null) {
                        delivery.id = child.key.toString()
                        if(delivery.description == deliveryDescription && delivery.progress == 0){
                            serviceDelivery = delivery
                            break
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        firebaseDatabase.reference.child("delivery_table").addValueEventListener(valueEventListener)
    }
}