package servicesLayer;

import com.google.gson.Gson;
import model.Address;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddressServices {

    public static List<Address> addressList= getAllAddresses();

    private void saveChangesToDB(){

        File tmpFile = new File("src/main/resources/database/tmpAddressFile");
        File oldFile = new File("src/main/resources/database/address");

        Gson gson = new Gson();

        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/database/tmpAddressFile" ,true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            addressList.forEach(address->{
                try {

                    String data= gson.toJson(address);
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

            addressList = getAllAddresses();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public static List<Address> getAllAddresses(){
        addressList = new ArrayList<>();

        try(FileReader fileReader = new FileReader("src/main/resources/database/address")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line= null;
            Gson gson = new Gson();

            Address address =null;
            while ((line= bufferedReader.readLine()) !=null){
                address =gson.fromJson(line , Address.class);
                addressList.add(address);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return addressList;
    }


    public Address createAddress(Address newAddress) throws IOException {

        long lines = 0;

        lines = Files.lines(Paths.get("src/main/resources/database/address")).count()+1;

        if(newAddress.getId() ==null){
            newAddress.setId(lines);
        }

        addressList.add(newAddress);
        saveChangesToDB();

        return newAddress;
    }


    public boolean isAddressFound(String country){
        return addressList.stream().anyMatch(address-> address.getCountry().equalsIgnoreCase(country));
    }


    public Address getAddressByCountryName(String country){
        List<Address> addresses = addressList.stream().filter(address -> address.getCountry().equalsIgnoreCase(country)).collect(Collectors.toList());

        if(addresses.size()>0){
            return addresses.get(0);
        }

        return null;
    }
}
