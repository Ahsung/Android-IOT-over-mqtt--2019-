<?php
if(isset($_POST['username']) && isset($_POST['password'])){
  $name = $_POST["name"];
  $age = $_POST["age"];
  $username = $_POST["username"];
  $password = $_POST["password"];
  
    $con = mysqli_connect("localhost","root","PASSWORD","login");
    $insert = 'insert into list(name,username,age,password) VALUES(\''.$name.'\',\''.$username.'\',\''.$age.'\',\''.$password.'\')';
    $result = mysqli_query($con,$insert);
    $response = array();
  if($result == false){
	  //echo mysqli_error($con);
	   $response["success"] = false;
    }else{
    $response["success"] = true;
    }
    echo json_encode($response);
}else{
  //header('Location:/index.php');
}
    
?>
