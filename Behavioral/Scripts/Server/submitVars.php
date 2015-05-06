<?
$version = "lit4";

//GET IP
$ip = (getenv(HTTP_X_FORWARDED_FOR))
    ?  getenv(HTTP_X_FORWARDED_FOR)
    :  getenv(REMOTE_ADDR);

//experimental data

$id = $_POST[id];
$condition = $_POST[condition];
$opType = $_POST[opType];
$opChoice = $_POST[opChoice];
$choice = $_POST[choice];
$payoffSubj1 = $_POST[payoffSubj1];
$payoffSubj2 = $_POST[payoffSubj1];
$payoffOpp1 = $_POST[payoffOpp1];
$payoffOpp2 = $_POST[payoffOpp2];
$rt = $_POST[rt];
$score = $_POST[score];
$globalRound = $_POST[globalRound];
$matchRound = $_POST[matchRound];

//add timestamp
$dateStamp = date("Y-m-j"); 
$timeStamp = date("H:i:s");

//send to database
$user="moral";
$password="j|n321";
$database="adam";
mysql_connect("localhost",$user,$password);
@mysql_select_db($database) or die( "Unable to select database");

$query1= "INSERT INTO stealpunish VALUES ('$id','$dateStamp','$timeStamp','$version','$condition','$opType','$opChoice','$choice','$payoffSubj1','$payoffSubj2','$payoffOpp1','$payoffOpp2','$rt','$score','$globalRound','$matchRound','')";
mysql_query($query1);

mysql_close();
?> 