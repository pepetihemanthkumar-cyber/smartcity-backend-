package com.smartcity.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private CityRepository repo;

    // ✅ GET ALL
    @GetMapping
    public List<City> getAllCities() {
        return repo.findAll();
    }

    // ✅ ADD CITY
    @PostMapping
    public City addCity(@RequestBody City city) {
        return repo.save(city);
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public City getCity(@PathVariable Long id) {
        return repo.findById(id).orElse(null);
    }

    // ✅ UPDATE CITY (NEW 🔥)
    @PutMapping("/{id}")
    public City updateCity(@PathVariable Long id, @RequestBody City city) {

        City existing = repo.findById(id).orElse(null);

        if (existing != null) {
            existing.setName(city.getName());
            existing.setLocation(city.getLocation());
            existing.setPopulation(city.getPopulation());
            return repo.save(existing);
        }

        return null;
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String deleteCity(@PathVariable Long id) {
        repo.deleteById(id);
        return "Deleted Successfully";
    }
}