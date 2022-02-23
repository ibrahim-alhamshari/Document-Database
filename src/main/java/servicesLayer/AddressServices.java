package servicesLayer;

import com.google.gson.Gson;
import model.Address;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class AddressServices {


    private static volatile AddressServices instance;
//    private static List<Address> addressList;
    private static HashMap<String , Address> hashMap;
    private static Lock lock = new ReentrantLock(true);
    private static long index; //The index for the new address
    private static HashMap<String, Address> cacheHashMap; //It represents the cache in my application


    private AddressServices(){
        instance = null;
        hashMap = getAllDataFromDB();
        index = initializeIndex();
        cacheHashMap = new HashMap<>();
    }


    public static AddressServices getInstance() {

        if (instance == null) {
            lock.lock();
                if (instance == null) {
                    instance = new AddressServices();
                }
            lock.unlock();
        }
        return instance;
    }


    private long initializeIndex(){

        if(hashMap.isEmpty()){

            index = 1; //no objects in the DB.

        }else {
            //Get the index of last object in the DB and increment it by 1.
            index = 1;
            hashMap.entrySet().forEach(e ->{
                if(e.getValue().getId()>index){
                    index = e.getValue().getId() + 1;
                }
            });
        }

        return index;
    }


    //Save any change for the addresses in DB.
    private void saveChangesToDB() {

        lock.lock();

        try {
            File tmpFile = new File("src/main/resources/database/tmpAddressFile");
            File oldFile = new File("src/main/resources/database/address");
            Gson gson = new Gson();

            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpAddressFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            hashMap.entrySet().forEach(item -> {
                try {

                    String data = gson.toJson(item.getValue());
                    bufferedWriter.write(data);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/database/address"));

            hashMap = new HashMap<>();
            cacheHashMap = new HashMap<>();// When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    private static HashMap<String, Address> getAllDataFromDB() {
        lock.lock();

        try (FileReader fileReader = new FileReader("src/main/resources/database/address");
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line = null;
            Gson gson = new Gson();

            hashMap = new HashMap<>();

            Address address = null;
            while ((line = bufferedReader.readLine()) != null) {
                address = gson.fromJson(line, Address.class);
                hashMap.put(address.getCountry(), address);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return hashMap;
    }



    public HashMap<String , Address> getDataFromAddressList(){
        return hashMap;
    }


    public Address createAddress(Address newAddress) throws IOException {

        newAddress.setId(index);
        hashMap.put(newAddress.getCountry() , newAddress);
        saveChangesToDB();
        index++;
        return newAddress;
    }


    public boolean isAddressFound(String country) {
        return hashMap.containsKey(country);
    }


    public Address getAddressByCountryName(String country) {

        Address address = cacheHashMap.get(country);// Try to get data from cache.
        if(Objects.nonNull(address)){
            return address;
        }

       Address tmpAddress = hashMap.get(country);

        if (Objects.nonNull(tmpAddress)) { // It will be on address in the list because the country is unique.
            cacheHashMap.put(country , tmpAddress);
            return tmpAddress;
        }

        return new Address();
    }

}
