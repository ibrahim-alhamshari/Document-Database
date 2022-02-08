package servicesLayer;

import com.google.gson.Gson;
import model.Address;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class AddressServices {


    private static volatile AddressServices instance;
    private static List<Address> cacheList;
    private static Lock lock = new ReentrantLock(true);
    private static long index; //The index for the new address


    private AddressServices(){
        instance = null;
        cacheList = getAllDataFromDB();
        index = initializeIndex();
    }


    public static AddressServices getInstance() {

        if(instance==null){
            lock.lock();

            if(instance==null){
                instance = new AddressServices();
            }

            lock.unlock();
        }
        return instance;
    }


    private long initializeIndex(){
        if(cacheList.size()>0){
            //Get the index of last object in the DB and increment it by 1.
            index = cacheList.get(cacheList.size()-1).getId() +1;
        }else {
            index=1; //No objects in the DB.
        }

        return index;
    }


    //Save any change for the addresses in DB.
    private void saveChangesToDB() {

        try {
            File tmpFile = new File("src/main/resources/database/tmpAddressFile");
            File oldFile = new File("src/main/resources/database/address");
            Gson gson = new Gson();

            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpAddressFile", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            cacheList.forEach(address -> {
                try {

                    String data = gson.toJson(address);
                    bufferedWriter.write(data);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            bufferedWriter.close();
            fileWriter.close();
            oldFile.delete();
            tmpFile.renameTo(new File("src/main/resources/database/address"));

            cacheList = getAllDataFromDB(); // When the DB updated, we have to update the cache.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<Address> getAllDataFromDB() {

        try (FileReader fileReader = new FileReader("src/main/resources/database/address")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Gson gson = new Gson();

            cacheList = new ArrayList<>();

            Address address = null;
            while ((line = bufferedReader.readLine()) != null) {
                address = gson.fromJson(line, Address.class);
                cacheList.add(address);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cacheList;
    }


    //It represents the cache in my application
    public List<Address> getDataFromCache(){
        return cacheList;
    }


    public Address createAddress(Address newAddress) throws IOException {

        long index = cacheList.size();
        newAddress.setId(index);
        cacheList.add(newAddress);
        saveChangesToDB();

        return newAddress;
    }


    public boolean isAddressFound(String country) {
        return cacheList.stream().anyMatch(address -> address.getCountry().equalsIgnoreCase(country));
    }


    public Address getAddressByCountryName(String country) {
        List<Address> addresses = cacheList.stream().filter(address -> address.getCountry().equalsIgnoreCase(country)).collect(Collectors.toList());

        if (addresses.size() > 0) {
            return addresses.get(0);
        }

        return new Address();
    }

}
