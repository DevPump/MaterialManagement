<?php
$postdata = file_get_contents("php://input");
$decode = json_decode($postdata);
//$decode->typeOfAction = $decode->typeOfAction ." " . rand() . " blash";
$returnData = new stdClass();
if($decode->barCode == "039800107978"){
  $returnData->itemName = "1A";
  $returnData->itemQuantity = "2A";
  $returnData->itemQuantityType = "3A";
} else {
  $returnData->itemName = "1B";
  $returnData->itemQuantity = "2B";
  $returnData->itemQuantityType = "3B";
}

// Return the data to the application.
echo json_encode($returnData);
