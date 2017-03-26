<?php
//get POST Data from STDIN.
$postdata = file_get_contents("php://input");
//json_encode(Array('actionType' => 'insert', 'barCode' => "85192004014"));

if($postdata != ''){
  require 'db.php';
  $decodedJSONObject = json_decode($postdata);
  switch ($decodedJSONObject->actionType) {
    case 'select':
      require_once 'select.php';
      selectData($databaseConnection, $decodedJSONObject);
    break;
    case 'insert':
      require_once 'insert.php';
      insertMaterial($databaseConnection, $decodedJSONObject);
    default:
      echo json_encode(Array("status" => "No action selected."));
    break;
  }
}
