<?php

$isDev = true;
if($isDev){
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
}else{
  ini_set('display_errors', 0);
  ini_set('display_startup_errors', 0);
  error_reporting(E_NONE);
}
  $databaseConnection = new PDO("pgsql:dbname=database;host=127.0.0.1","user","password");
