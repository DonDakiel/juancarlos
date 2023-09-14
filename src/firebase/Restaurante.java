package firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;
import javax.swing.JOptionPane;
public class Restaurante {

    public static void main(String[] args) throws FileNotFoundException {
        String perro, malta, pan,nombre;
        double compra=0;
        FirebaseSaveObject fire=new FirebaseSaveObject(); 
        Item item = new Item();
        System.out.println("bienvenido al restaurante");
        System.out.println("que desea?");
        int opcion = Integer.parseInt(JOptionPane.showInputDialog("nos queda pan(1), perro(2) o malta(3), digite el numero de su eleccion"));
            switch (opcion) {
                case (1):
                    System.out.println("cuesta 2000");
                        compra = 2000;
                    break;
                case (2):
                    System.out.println("cuesta 5000");
                        compra = 5000;
                   
                    break;
                case (3):
                    System.out.println("cuesta 3000");
                        compra = 3000;
                    
                    break;
            }
                nombre=JOptionPane.showInputDialog("nombre");    
                item.setName(nombre);
                item.setPrice(compra);
                item.setId("1");
                new Restaurante().save(item);
                
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
}

