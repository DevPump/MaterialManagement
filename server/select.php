<?php
function selectData($dbConn, $decodedJSONObject){
  $sqlExecute = $dbConn->prepare('SELECT "barCode","itemName","itemQuantity" FROM "BarCodeMain" WHERE "barCode" = :barCode');
  $sqlExecute->bindParam(':barCode',$decodedJSONObject->barCode);
  $sqlExecute->execute();
  $results = ($sqlExecute->fetch(PDO::FETCH_ASSOC));
  if(!empty($results)){
    echo json_encode($results);
  } else{
    echo json_encode(Array("select status" => "Item Not in Database"));
  }
}
