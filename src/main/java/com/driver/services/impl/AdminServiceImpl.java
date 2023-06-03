package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);

        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        List<ServiceProvider> serviceProviders = admin.getServiceProviders();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);

        serviceProviders.add(serviceProvider);
        admin.setServiceProviders(serviceProviders);

        serviceProvider.setAdmin(admin);

        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        String capital = countryName.toUpperCase();

        if (!capital.equals("IND") || !capital.equals("USA") || !capital.equals("JPN") || !capital.equals("AUS") || !capital.equals("CHI")) throw new Exception("Country not found");

        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        Country country = new Country();

        CountryName name = CountryName.valueOf(capital);
        country.setCountryName(name);
        country.setCode(name.toCode());

        List<Country> countryList = serviceProvider.getCountryList();
        countryList.add(country);

        country.setServiceProvider(serviceProvider);
        serviceProvider.setCountryList(countryList);

        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;
    }
}
