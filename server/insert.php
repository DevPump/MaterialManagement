<?php
function insertMaterial($dbConn, $decodedJSONObject){
  $sqlExecute = $dbConn->prepare('INSERT INTO "BarCodeMain" VALUES(:barCode,:itemName,:itemQuantity);');
  $sqlExecute->bindParam(':barCode',$decodedJSONObject->barCode);
  $sqlExecute->bindParam(':itemName',$decodedJSONObject->itemName);
  $sqlExecute->bindParam(':itemQuantity',$decodedJSONObject->itemQuantity);
  //Check if insert was successful.
  if($sqlExecute->execute()){
    echo json_encode(Array("status" => "Insert Success"));
  } else{
    echo json_encode(Array("status" => "Insert Failed"));
  }
}
