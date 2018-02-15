<html>
   <head>
      <style>
         .o{
            background-color : #e8e9ea;
            text-decoration : none;
            color : black;
         }
         .e{
            background-color : #f4f6f7;
            text-decoration : none;
            color : black;
         }
      </style>
     </head>
   <body>
      <h1>GOLGOL</h1>
      <table>
	<form url="input" method="get">
         <tr style="font-size:1.5em;">
            <td  class="o">Search Keys <input type = "text" name="key" value=""/></td>
	    <td class="o">
	    <select name="branch" style="font-size:0.7em; width:160px;">
	   <option value="all">All branch</option>
		<option value="1st">First Year(B. tech)</option>
		<option value="app">Applied Science</option>
		<option value="humanities">School of Humanities and social Sciences</option>
		<option value="cse">School of Computer Engineering</option>
		<option value="etc">School of Electronics Engineering</option>
		<option value="ee">School of Electrical Engineering</option>
		<option value="mech.">School of Mechanical Engineering</option>
		<option value="civil">School of Civil Engineering</option>
	    </select>
	    <input type="submit" style="position: absolute; left: -9999px"/>
	    </td>
         </tr>
	</form>
	 <tr>
	    <td colspan="2" style="font-size: 0.9em">Enter Search Keys separated by comma for better results. for example: CS -1001, Programming in C</td>
	 </tr>
	 <tr>
	    <td colspan="2">--------------------------------------------------------------------------------------------------------------------------</td>
	 </tr>
	 <?php
		function queryMysql($query){		
			$dbhost = 'localhost';
			$dbname = 'KIITQS';
			$dbuser = 'sneh';
			$dbpass = 'mypass';
			$connect = new mysqli($dbhost,$dbuser,$dbpass,$dbname);
			if($connect->connect_error) die($connect->connect_error);
		
	  		$result =$connect->query($query);
      			if(!$result) 
	     		die($connect->error);
	 		return $result;
  		}
		function inputCheck($var){
			$dbhost = 'localhost';
			$dbname = 'KIITQS';
			$dbuser = 'sneh';
			$dbpass = 'mypass';
			$connect = new mysqli($dbhost,$dbuser,$dbpass,$dbname);
			if($connect->connect_error) die($connect->connect_error);

        		$var= strip_tags($var);
			$var= htmlentities($var);
			$var= stripslashes($var);
			$var= mysqli_real_escape_string($connect,$var);
			return $var;
		}
		function processKey($key){
			$key = strtoupper($key);
			$temp = "'%";
			for($i=0; $i<strlen($key); $i++){
				if($key[$i] <= 'Z' && $key[$i] >= 'A')
					$temp = $temp.$key[$i];
				else if($key[$i] <= '9' && $key[$i] >= '0')
					$temp = $temp.$key[$i];
				else if($key[$i] == ',')
					$temp = $temp."%' or searchKey like '%";
			}
			$temp = $temp."%'";
			return $temp;
		}
		if(isset($_GET['key']) && isset($_GET['branch'])){
			
			$key = inputCheck($_GET['key']);
			$branch = inputCheck($_GET['branch']);
			$key = processKey($key);
			
			$query = "";
			if($_GET['branch'] == "all"){
				$query = "select * from pdfFile where searchKey like $key limit 50";
			}
			else{
				$query = "select * from pdfFile where searchKey like $key and branch = '$branch' limit 50";
			}
			
			$result = queryMysql($query);
			$n = $result->num_rows;
			if($n == 0)
				echo' <td colspan="2" class="o" style="color:red;">No result found! Make sure all words are spelled correctly</td>';
			else{
				$i=0;
				while($row = $result->fetch_assoc()){
					if($i%2 == 0)
						echo "<tr><td class='o' colspan='2'><a target=' ' href='".$row['link']."'>".$row['name']."</a></td></tr>";
					else
						echo "<tr><td class='e' colspan='2'><a target=' ' href='".$row['link']."'>".$row['name']."</a></td></tr>";		
				$i++;
				}
			}
		}
	 ?>
      </table>
      <p style="position: relative; bottom:0px">hello world!</p>
   </body>
</html>
