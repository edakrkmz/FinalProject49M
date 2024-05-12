package com.edakorkmaz.kotlinfinal.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.edakorkmaz.kotlinfinal.R
import com.edakorkmaz.kotlinfinal.adapter.FeedRecyclerAdapter
import com.edakorkmaz.kotlinfinal.databinding.ActivityMyoutfitsBinding
import com.edakorkmaz.kotlinfinal.model.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class MyOutfitsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMyoutfitsBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var myoutfitsAdapter : FeedRecyclerAdapter
    private lateinit var storage : FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyoutfitsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        auth = Firebase.auth
        db = Firebase.firestore
        storage = Firebase.storage

        postArrayList = ArrayList<Post>()

        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        myoutfitsAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = myoutfitsAdapter
    }

    private fun getData() {

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if(error != null) {
                Toast.makeText(this,error.localizedMessage, Toast.LENGTH_LONG).show()

            } else {
                if(value != null) {
                    if(!value.isEmpty) {

                        val documents = value.documents

                        postArrayList.clear()

                        for (document in documents) {
                            //casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            println(comment)

                            val post = Post(userEmail, comment, downloadUrl)
                            postArrayList.add(post)
                        }

                        myoutfitsAdapter.notifyDataSetChanged()
                    }
                }
            }


        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {


        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.inspoutfitmenu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.add_clothing) {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.signout) {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}