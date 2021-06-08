package com.mandatory2.repo;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mandatory2.TaskListener;
import com.mandatory2.Updatable;
import com.mandatory2.model.Snapinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                GeoPoint imageLoc = snap.getGeoPoint("imageLoc");
                if(title != null){
                    notes.add(new Snapinfo(snap.getId(),title.toString(),url.toString(),imageLoc));
                }
                System.out.println("Snap: " + snap.toString());
            }

            activity.update(null);
        });
    }
    public void getimage(String url,TaskListener taskListener) {
        StorageReference ref = storage.getReference().child(url);
        int max = 1024 * 1024;
        ref.getBytes(max).addOnSuccessListener(bytes -> {
            taskListener.receive(bytes);
        }).addOnFailureListener(exception -> {
            System.out.println("fejl ved download" + exception);
        });
    }

    public void addNote(String text, String url, GeoPoint imageLoc) {
        // insert a new note with "new note"
        DocumentReference ref = db.collection(NOTES).document();
        Map<String,Object> map = new HashMap<>();
        map.put("title", text);
        map.put("url", url);
        map.put("imageLoc", imageLoc);
        ref.set(map); // will replace any previous value.
        //db.collection("notes").add(map); // short version
        System.out.println("Done inserting new document " + ref.getId());
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

}
