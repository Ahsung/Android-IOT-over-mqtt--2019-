<?php
$con = mysqli_connect("localhost", "root", "PASSWORD", "login");

$username = $_POST["username"];
$password = $_POST["password"];

$check = "SELECT * FROM list WHERE username = '$username'";
$result = $con->query($check);
$response = array();
$response["success"] = 3;
if($result->num_rows==1){
  $row = $result->fetch_array(MYSQLI_ASSOC);
  if($row['password']==$password){
	$response["success"] = 1;
	$response["name"] = $row["name"];
	$response["username"] = $username;
	$response["password"] = $password;
	$response["age"] = $row["age"];
	$response["message1"] = $row["message1"];
  }
  else{
	$response["success"] = 2;
  }

}else{
	$response["success"] = 3;
}

echo json_encode($response);

?>
