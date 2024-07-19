    package com.app.contacteasypermissions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val RC_CONTACTS_PERM = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.display_contact).setOnClickListener(
            {
                // Request contacts permission
                requestContactsPermission()
            }
        )

    }

    private fun requestContactsPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            // Already have permission, do the thing
            getContactList()
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,"This app needs access to your contacts to display them.",
                RC_CONTACTS_PERM,
                Manifest.permission.READ_CONTACTS
            )
        }
    }

    @AfterPermissionGranted(RC_CONTACTS_PERM)
    private fun getContactList() {
        val contactList = mutableListOf<String>()
        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (nameIndex != -1 && phoneIndex != -1) {
                while (it.moveToNext()) {
                    val name = it.getString(nameIndex)
                    val phoneNumber = it.getString(phoneIndex)
                    contactList.add("$name: $phoneNumber")
                }
            }
            it.close()
        }

        findViewById<TextView>(R.id.contact_textView).setText(contactList.joinToString("\n"))
       // Toast.makeText(this, contactList.joinToString("\n"), Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        //
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }
}
