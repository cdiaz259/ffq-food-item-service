package edu.fiu.ffqr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.fiu.ffqr.calculator.FFQCalculator;
import edu.fiu.ffqr.models.FFQResult;
import edu.fiu.ffqr.models.FoodItem;
import edu.fiu.ffqr.models.FoodItemInput;
import edu.fiu.ffqr.models.NutrientList;
import edu.fiu.ffqr.service.FFQFoodEntryService;
import edu.fiu.ffqr.service.NutrientListService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class FoodItemController {
  @Autowired
  private FFQFoodEntryService foodItemService;
  @Autowired
  private NutrientListService foodTypeService;
  
  public FoodItemController() {}
  
  @GetMapping("/fooditems")
  public List<FoodItem> getAllFoods() throws JsonProcessingException {
	  List<FoodItem> foods = foodItemService.getAll();
	  return foods;
  }
  
  @GetMapping("/{name}")
  public FoodItem getItemsWithName(@PathVariable("name") String name) throws JsonProcessingException {
	  return foodItemService.getEntryWithName(name);
  }
  
  @GetMapping("/getByID/{nutrientListID}")
  public FoodItem getItemWithID(@PathVariable("nutrientListID") String nutrientListID) {
	  return foodItemService.getEntryWithNutrientListID(nutrientListID);
  }
  
  @PostMapping("/create")
  public FoodItem create(@RequestBody FoodItem newItem) {
	  
	  FoodItem fi = null;
	  if (foodItemService.getEntryWithName(newItem.getName()) != null) {
		  throw new IllegalArgumentException("A record with name " + newItem.getName() + " already exists");
	  }
	  
	  //check nutrientListID is in nutrient list collection 
	  for (int i = 0; i < newItem.getFoodTypes().size(); i++) {
		  String nutrientListID = newItem.getFoodTypes().get(i).getNutrientListID();
		  NutrientList matchingID = foodTypeService.getWithNutrientListID(nutrientListID);
		  if (matchingID == null) {
			  throw new IllegalArgumentException(nutrientListID + " is not in the nutrientList collection");
		  }
		  
	  }
	  
	  //check that none of the nutrientListIDs are used in other food items
	  for (int i = 0; i < newItem.getFoodTypes().size(); i++) {
		  if(foodItemService.getEntryWithNutrientListID(newItem.getFoodTypes().get(i).getNutrientListID()) != null) {
			  String foodItemName = foodItemService.getEntryWithNutrientListID(newItem.getFoodTypes().get(i).getNutrientListID()).getName();
			  throw new IllegalArgumentException(newItem.getFoodTypes().get(i).getNutrientListID() + " is already used in a different food item, " + foodItemName
					  + " and cannot be used in item " + newItem.getName());
		  }
	  }
	  
	  if (newItem.getServingsList() == null && newItem.getAdditionalSugar() == null && newItem.isPrimary()) {
		  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes(), newItem.isPrimary());
		  foodItemService.create(fi);  
	  }
	  else if (newItem.getServingsList() == null && newItem.getAdditionalSugar() == null) {
		  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes());
		  foodItemService.create(fi);  
	  }  
	  else if (newItem.getAdditionalSugar() == null && !newItem.isPrimary()) {
		  fi = new FoodItem(newItem.getName(), newItem.getServingsList(), newItem.getFoodTypes());
		  foodItemService.create(fi);
	  }
	  else if (newItem.getAdditionalSugar() == null && newItem.isPrimary()) {
		  fi = new FoodItem(newItem.getName(), newItem.getServingsList(), newItem.getFoodTypes(), newItem.isPrimary());
		  foodItemService.create(fi);
	  }
	  else if (newItem.getServingsList() == null && !newItem.isPrimary()) {
		  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes(), newItem.getAdditionalSugar());
		  foodItemService.create(fi);
	  }
	  else if (newItem.getServingsList() == null && newItem.isPrimary()) {
		  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes(), newItem.getAdditionalSugar(), newItem.isPrimary());
		  foodItemService.create(fi);
	  }
	  else {
		  fi = new FoodItem(newItem.getName(), newItem.getServingsList(), newItem.getFoodTypes(), newItem.getAdditionalSugar());
		  foodItemService.create(fi);
	  }
	  
	  return fi;
  }
  
  @PostMapping("/createMany")
  public List<FoodItem> create(@RequestBody ArrayList<FoodItem> data) {
	  for (FoodItem newItem: data) {
		  FoodItem fi = null;
		  if (foodItemService.getEntryWithName(newItem.getName()) != null) {
			  throw new IllegalArgumentException("A record with name " + newItem.getName() + " already exists");
		  }
		  
		  //check nutrientListID is in nutrient list collection 
		  for (int i = 0; i < newItem.getFoodTypes().size(); i++) {
			  String nutrientListID = newItem.getFoodTypes().get(i).getNutrientListID();
			  NutrientList matchingID = foodTypeService.getWithNutrientListID(nutrientListID);
			  if (matchingID == null) {
				  throw new IllegalArgumentException(nutrientListID + " is not in the nutrientList collection");
			  }
			  
		  }
		  
		  //check that none of the nutrientListIDs are used in other food items
		  for (int i = 0; i < newItem.getFoodTypes().size(); i++) {
			  if(foodItemService.getEntryWithNutrientListID(newItem.getFoodTypes().get(i).getNutrientListID()) != null) {
				  String foodItemName = foodItemService.getEntryWithNutrientListID(newItem.getFoodTypes().get(i).getNutrientListID()).getName();
				  throw new IllegalArgumentException(newItem.getFoodTypes().get(i).getNutrientListID() + " is already used in a different food item, " + foodItemName
						  + " and cannot be used in item " + newItem.getName());
			  }
		  }
		  
		  if (newItem.getServingsList() == null && newItem.getAdditionalSugar() == null && newItem.isPrimary()) {
			  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes(), newItem.isPrimary());
			  foodItemService.create(fi);  
		  }
		  else if (newItem.getServingsList() == null && newItem.getAdditionalSugar() == null) {
			  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes());
			  foodItemService.create(fi);  
		  }  
		  else if (newItem.getAdditionalSugar() == null && !newItem.isPrimary()) {
			  fi = new FoodItem(newItem.getName(), newItem.getServingsList(), newItem.getFoodTypes());
			  foodItemService.create(fi);
		  }
		  else if (newItem.getAdditionalSugar() == null && newItem.isPrimary()) {
			  fi = new FoodItem(newItem.getName(), newItem.getServingsList(), newItem.getFoodTypes(), newItem.isPrimary());
			  foodItemService.create(fi);
		  }
		  else if (newItem.getServingsList() == null && !newItem.isPrimary()) {
			  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes(), newItem.getAdditionalSugar());
			  foodItemService.create(fi);
		  }
		  else if (newItem.getServingsList() == null && newItem.isPrimary()) {
			  fi = new FoodItem(newItem.getName(), newItem.getFoodTypes(), newItem.getAdditionalSugar(), newItem.isPrimary());
			  foodItemService.create(fi);
		  }
		  else {
			  fi = new FoodItem(newItem.getName(), newItem.getServingsList(), newItem.getFoodTypes(), newItem.getAdditionalSugar());
			  foodItemService.create(fi);
		  }

	  }
	  
	  return data;
  }
  
  @DeleteMapping("/delete")
  public String delete(@RequestParam String name) {
	  //find nutrientListID for this food item
	  FoodItem target = foodItemService.getEntryWithName(name);
	  ArrayList<String> nutrientListIDS = new ArrayList<String>();
	  
	  for (int i = 0; i < target.getFoodTypes().size(); i++) {
		  nutrientListIDS.add(target.getFoodTypes().get(i).getNutrientListID());
	  }
	  
	  //delete these nutrientListIDS in nutrientList collection
	  for (int i = 0; i < nutrientListIDS.size(); i++) {
		  NutrientList nl = foodTypeService.getWithNutrientListID(nutrientListIDS.get(i));
		  foodTypeService.delete(nl.getNutrientListID());
	  }
	  
	  foodItemService.delete(name);
  	  return "Deleted " + name;
  }
  
  @PostMapping("/calculate") 
  public FFQResult calculateTotals(@RequestBody ArrayList<FoodItemInput> userChoices) {
	  return FFQCalculator.calculateTotals(userChoices, foodTypeService);
  }
  
}