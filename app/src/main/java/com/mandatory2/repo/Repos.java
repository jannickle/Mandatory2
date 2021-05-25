package com.mandatory2.repo;

import android.graphics.Bitmap;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mandatory2.TaskListener;
import com.mandatory2.Updatable;
import com.mandatory2.model.Snapinfo;
import com.mandatory2.model.Title;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Repos {

    private static Repos repos = new Repos();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    public List<Snapinfo> notes = new ArrayList<>(); // you could use Note, instead of String
    private final String NOTES = "snapinfo";
    private Updatable activity;
    public static Repos r(){
        return repos;
    }

    public void setup(Updatable a, List<Snapinfo> list){
        activity = a;
        notes = list;
        startListener();
    }

    public Snapinfo getNoteWith(String id){
        for(Snapinfo snapinfo : notes){
            if(snapinfo.getId().equals(id)){
                return snapinfo;
            }
        }
        return null;
    }

    public void startListener(){
        db.collection(NOTES).addSnapshotListener((values, error) ->{
            notes.clear();
            for(DocumentSnapshot snap: values.getDocuments()){
                Object title = snap.get("title");
                Object url = snap.get("url");
                if(title != null){
                    notes.add(new Snapinfo(snap.getId(),title.toString(),url.toString()));
                }
                System.out.println("Snap: " + snap.toString());
            }
            // have a reference to MainActivity, and call a update()
            activity.update(null);
        });
    }
    public void getimage(String url,TaskListener taskListener){
    StorageReference ref = storage.getReference().child(url);
        int max = 1024 * 1024;
    ref.getBytes(max).addOnSuccessListener(bytes -> {
        taskListener.receive(bytes);
    }).addOnFailureListener(exception -> {
        System.out.println("fejl ved download" + exception);
    });
    }
    public void addNote(String text, String url) {
        // insert a new note with "new note"
        DocumentReference ref = db.collection(NOTES).document();
        Map<String,String> map = new HashMap<>();
        map.put("title", text);
        map.put("url", url);
        ref.set(map); // will replace any previous value.
        //db.collection("notes").add(map); // short version
        System.out.println("Done inserting new document " + ref.getId());
    }
    public void updateNote(Title title) {
        DocumentReference ref = db.collection(NOTES).document(title.getId());
        Map<String,String> map = new HashMap<>();
        map.put("title", title.getText());
        ref.set(map); // will replace any previous value.
        //ref.update("key", "value"); // for updating single values, instead of the whole document.
        System.out.println("Done updating document " + ref.getId());
    }
    public void deleteimage(String url){
        StorageReference ref = storage.getReference();
        StorageReference deleref = ref.child(url);
        deleref.delete();
    }
    public void deleteNote(String id){
        DocumentReference ref = db.collection(NOTES).document(id);
        ref.delete();
    }

    public void updateNoteAndImage(Title title, Bitmap bitmap) {
        updateNote(title);
        System.out.println("uploadBitmap called " + bitmap.getByteCount());
        StorageReference ref = storage.getReference(title.getId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        ref.putBytes(baos.toByteArray()).addOnCompleteListener(snap -> {
            System.out.println("OK to upload " + snap);
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });
    }

    public void downloadBitmap(String id, TaskListener taskListener){ // when to call this method?
        StorageReference ref = storage.getReference(id);
        int max = 1024 * 1024; // you are free to set the limit here
        ref.getBytes(max).addOnSuccessListener(bytes -> {
            taskListener.receive(bytes); // god linie!
            System.out.println("Download OK");
        }).addOnFailureListener(ex -> {
            System.out.println("error in download " + ex);
        });
    }
}
