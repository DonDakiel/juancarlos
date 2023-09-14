package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import firebase.Item;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.Map;



public class FirebaseSaveObject {

    public static void main(String[] args) throws FileNotFoundException {
        Item item = new Item();
        
//        new FirebaseSaveObject().updateItem("item", "pice", 1234D, 200);
//        new FirebaseSaveObject().delete("nsme");
        new FirebaseSaveObject().save(item);
//        new FirebaseSaveObject().recover();
    }

    private FirebaseDatabase firebaseDatabase;

 
    public void initFirebase() {
        
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()   
                    .setDatabaseUrl("https://adsxa-efeec-default-rtdb.firebaseio.com/")
                    .setServiceAccount(new FileInputStream(new File("C:\\Users\\ESTUDIANTE\\Desktop\\adsxa-efeec-firebase-adminsdk-gurl5-bb200ad531.json")))
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
            System.out.println("Conexión exitosa....");
        }catch (RuntimeException ex) {
            System.out.println("Problema ejecucion ");
        }catch (FileNotFoundException ex) {
            System.out.println("Problema archivo");
        }

         
    }

    public void save(Item item) throws FileNotFoundException {
        if (item != null) {
            initFirebase();
            
            /* Get database root reference */
            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            
            /* Get existing child or will be created new child. */
            DatabaseReference childReference = databaseReference.child("facturas").child(item.getId());

            /**
             * The Firebase Java client uses daemon threads, meaning it will not prevent a process from exiting.
             * So we'll wait(countDownLatch.await()) until firebase saves record. Then decrement `countDownLatch` value
             * using `countDownLatch.countDown()` and application will continues its execution.
             */
            CountDownLatch countDownLatch = new CountDownLatch(1);
            childReference.setValue(item, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    System.out.println("Registro guardado!");
                    // decrement countDownLatch value and application will be continues its execution.
                    countDownLatch.countDown();
                }
            });
            try {
                //wait for firebase to saves record.
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void recover() {
        
            initFirebase();

            /* Get database root reference */
            DatabaseReference databaseReference = firebaseDatabase.getReference("item");

            /* Get existing child or will be created new child. */
            //DatabaseReference childReference = databaseReference.child("item");

            /**
             * The Firebase Java client uses daemon threads, meaning it will not
             * prevent a process from exiting. So we'll
             * wait(countDownLatch.await()) until firebase saves record. Then
             * decrement `countDownLatch` value using
             * `countDownLatch.countDown()` and application will continues its
             * execution.
             */
            CountDownLatch countDownLatch = new CountDownLatch(1);
            databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        Item value = dataSnapshot.getValue(Item.class);
                       // System.out.println("ID: "+ value.getId());
                        System.out.println("nombre: "+ value.getName());
                        System.out.println("precio: "+ value.getPrice());
                        
                        countDownLatch.countDown();

                        //Log.d(TAG, "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                       // Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
        try {
            //wait for firebase to saves record.
            countDownLatch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        
            }
    
    private void delete(String itemId) {
    if (itemId != null) {
        initFirebase();

        DatabaseReference databaseReference = firebaseDatabase.getReference("/");
        DatabaseReference itemReference = databaseReference.child("item").child(itemId);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        
        itemReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de == null) {
                    System.out.println("Registro eliminado exitosamente!");
                } else {
                    System.err.println("Error al eliminar el registro: " + de.getMessage());
                }
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
    
    private void updateItem(String itemId, String newName, Double newPrice, long newId) {
        if (itemId != null) {
            initFirebase();

            DatabaseReference databaseReference = firebaseDatabase.getReference("/");
            DatabaseReference itemReference = databaseReference.child(itemId);
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("price", newPrice);
            updates.put("id", newId);
            itemReference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        System.out.println("Registro actualizado exitosamente!");
                    } else {
                        System.err.println("Error al actualizar el registro: " + de.getMessage());
                    }
                    countDownLatch.countDown();
                }
            });

            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
//Realizar la recuperacion de informacion
//perfeccionar el metodo en que se realiza una operacion, suprimir el conteo regresivo