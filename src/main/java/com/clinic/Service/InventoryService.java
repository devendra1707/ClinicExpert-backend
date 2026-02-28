package com.clinic.Service;

import com.clinic.Model.Inventory;
import com.clinic.Repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Iterable<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Inventory updateInventory(Long id, Inventory inventory) {

        return inventoryRepository.findById(id).map(existingItem -> {

            existingItem.setName(inventory.getName());
            existingItem.setCategory(inventory.getCategory());
            existingItem.setQuantity(inventory.getQuantity());
            existingItem.setMinStock(inventory.getMinStock());
            existingItem.setUnit(inventory.getUnit());
            existingItem.setPrice(inventory.getPrice());
            existingItem.setExpiryDate(inventory.getExpiryDate());
            existingItem.setSupplier(inventory.getSupplier());
            existingItem.setBatchNumber(inventory.getBatchNumber());
            existingItem.setLastUpdated(java.time.LocalDateTime.now());
            existingItem.setStatus(inventory.getStatus());
            existingItem.setLocation(inventory.getLocation());
            existingItem.setReorderPoint(inventory.getReorderPoint());
            return inventoryRepository.save(existingItem);

        }).orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }


}
