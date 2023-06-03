package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        String capital = countryName.toUpperCase();
        CountryName name = CountryName.valueOf(capital);
        User user = userRepository2.findById(userId).get();

        if (user.getConnected()) throw new Exception("Already connected");

        if (user.getCountry().getCountryName().equals(name)) return user;

        List<ServiceProvider> serviceProviders = user.getServiceProviderList();
        int smallestId = Integer.MAX_VALUE;
        boolean serviceProviderFound = false;

        for (ServiceProvider serviceProvider: serviceProviders) {
            List<Country> countryList = serviceProvider.getCountryList();

            for (Country country: countryList) {
                if(country.getCountryName().equals(name)) {
                    serviceProviderFound = true;

                    smallestId = Math.min(smallestId, serviceProvider.getId());
                    user.setMaskedIp(country.getCode() + "." + serviceProvider.getId());
                    user.setConnected(true);
                }
            }
        }

        if (!serviceProviderFound) throw new Exception("Unable to connect");


        return userRepository2.save(user);
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();

        if (!user.getConnected()) throw new Exception("Already disconnected");

       user.setConnected(false);
       user.setMaskedIp(null);

//       ServiceProvider serviceProvider ;

        userRepository2.save(user);

        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

//        if sender and receiver are in the same country or receiver is connected to the senders original country
        if (sender.getCountry().getCountryName().equals(receiver.getCountry().getCountryName()) || sender.getCountry().getCode().equals(receiver.getMaskedIp().substring(0,3))) return sender;

//        if sender is connected to vpn and receiver is not
        if(sender.getConnected() && sender.getMaskedIp().substring(0, 3).equals(receiver.getCountry().getCode())) {

            Connection connection = new Connection();
            connection.setUser(receiver);
            connection.setServiceProvider(receiver.getCountry().getServiceProvider());

            sender.getConnectionList().add(connection);
            return userRepository2.save(sender);
        }

//        sender and receiver are not connected to vpn
        if (!sender.getConnected() && !receiver.getConnected()) {
            sender = connect(senderId, String.valueOf(receiver.getCountry()));

            Connection connection = new Connection();
            connection.setUser(receiver);
            connection.setServiceProvider(receiver.getCountry().getServiceProvider());

            sender.getConnectionList().add(connection);
            return userRepository2.save(sender);
        }

       throw new Exception("Cannot establish communication");
    }
}
