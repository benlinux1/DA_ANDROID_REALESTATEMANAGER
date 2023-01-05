package com.benlinux.realestatemanager.data.propertyRepository

import android.net.Uri
import android.util.Log
import com.benlinux.realestatemanager.ui.models.Property
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class PropertyRepository {

    companion object {

        // FIRESTORE DATA
        private const val COLLECTION_NAME = "properties"
        private const val PICTURES_FOLDER = "pictures"

        // Get the Collection Reference in Firestore Database
        fun getPropertiesCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
        }

        // Get all users from Firestore
        fun getAllPropertiesData(): Task<QuerySnapshot> {
            return getPropertiesCollection().get()
        }

        // Create User in Firestore
        fun createProperty(property: Property): Task<QuerySnapshot> {
            // Check if this property exists in database
            return getAllPropertiesData()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                // Get all properties
                val properties: List<Property> =
                    querySnapshot.toObjects(Property::class.java)
                var propertyExists = false
                // Check if user id exists in database
                for (i in properties.indices) {
                    if (properties[i].id == property.id) {
                        // If exists, set boolean to true
                        propertyExists = true
                        break
                    }
                }
                // If user exists, don't do anything
                if (propertyExists) {
                    Log.d("PROPERTY CREATION INFO", "property already exists")
                // If property doesn't exist, create it in FireStore
                } else {
                    getPropertiesCollection().document(property.id.toString()).set(property)
                }
            }
        }


        // Upload image from device to firebase storage
        fun uploadImageToFirestore(imageUri: Uri?): UploadTask {
            val uuid = UUID.randomUUID().toString() // GENERATE UNIQUE STRING
            val storageRef = FirebaseStorage.getInstance().reference
            val picturesRef = storageRef.child("$PICTURES_FOLDER/$uuid")
            return picturesRef.putFile(imageUri!!)
        }


        fun updateProperty(property: Property): Task<Void> {
            return getPropertiesCollection().document(property.id.toString()).set(property)
        }

    }
}