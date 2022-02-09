package servicesLayer;

import com.google.gson.Gson;
import model.Address;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class AddressServices {


    private static volatile AddressServices instance;
    private static List<Address> addressList;
    private static Lock lock = new ReentrantLock(true);
    private static long index; //The index for the new address
    private static Hashtable<String, Address> hashtable; //It represents the cache in my application


    private AddressServices(){
        instance = null;
        addressList = getAllDataFromDB();
        index = initializeIndex();
        hashtable = new Hashtable<>();
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
        if(addressList.size()>0){
            //Get the index of last object in the DB and increment it by 1.
            index = addressList.get(addressList.size()-1).getId() +1;
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

            addressList.forEach(address -> {
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

            addressList = getAllDataFromDB();
            hashtable = new Hashtable<>();// When the DB updated, we have to update the cache.

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<Address> getAllDataFromDB() {

        try (FileReader fileReader = new FileReader("src/main/resources/database/address")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Gson gson = new Gson();

            addressList = new ArrayList<>();

            Address address = null;
            while ((line = bufferedReader.readLine()) != null) {
                address = gson.fromJson(line, Address.class);
                addressList.add(address);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressList;
    }



    public List<Address> getDataFromAddressList(){
        return addressList;
    }


    public Address createAddress(Address newAddress) throws IOException {

        long index = addressList.size();
        newAddress.setId(index);
        addressList.add(newAddress);
        saveChangesToDB();

        return newAddress;
    }


    public boolean isAddressFound(String country) {
        return addressList.stream().anyMatch(address -> address.getCountry().equalsIgnoreCase(country));
    }


    public Address getAddressByCountryName(String country) {

        Address address = hashtable.get(country);// Try to get data from cache.
        if(Objects.nonNull(address)){
            return address;
        }

        List<Address> addressList = AddressServices.addressList.stream().filter(item -> item.getCountry().equalsIgnoreCase(country)).collect(Collectors.toList());

        if (addressList.size() > 0) { // It will be on address in the list because the country is unique.
            hashtable.put(country , addressList.get(0));
            return addressList.get(0);
        }

        return new Address();
    }

}
