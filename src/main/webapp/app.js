function doRegister() {
	var password = document.getElementById("password").value;
	var confirmation = document.getElementById("confirmation").value;
	var username = document.getElementById("username").value;
	var fullName = document.getElementById("fullName").value;
	var email = document.getElementById("email").value;
	var profileVisibility = document.getElementById("profileVisibility").value;
	var telephoneNumber = document.getElementById("telephoneNumber").value;
	var mobilePhoneNumber = document.getElementById("mobilePhoneNumber").value;
	var occupation = document.getElementById("occupation").value;
	var workPlace = document.getElementById("workPlace").value;
	var address = document.getElementById("address").value;
	var compAddress = document.getElementById("compAddress").value;
	var zip = document.getElementById("zip").value;
	var nif = document.getElementById("nif").value;

	if (profileVisibility == "") {
		profileVisibility = null;
	}
	if (telephoneNumber == "") {
		telephoneNumber = null;
	}
	if (mobilePhoneNumber == "") {
		mobilePhoneNumber = null;
	}
	if (occupation == "") {
		occupation = null;
	}
	if (workPlace == "") {
		workPlace = null;
	}
	if (address == "") {
		address = null;
	}
	if (compAddress == "") {
		compAddress = null;
	}
	if (zip == "") {
		zip = null;
	}
	if (nif == "") {
		nif = null;
	}

	var jsonData = {
		"username": username,
		"email": email,
		"fullName": fullName,
		"password": password,
		"confirmation": confirmation,
		"profileVisibility": profileVisibility,
		"telephoneNumber": telephoneNumber,
		"mobilePhoneNumber": mobilePhoneNumber,
		"occupation": occupation,
		"workPlace": workPlace,
		"address": address,
		"compAddress": compAddress,
		"zip": zip,
		"nif": nif
	}

	var req = new XMLHttpRequest();

	req.open("POST", "/rest/register/v1", true);
	req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	req.send(JSON.stringify(jsonData));

	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			if (req.status == 200) {
				alert(req.responseText);
				window.location.href = "../index.html";
			}
			else {
				alert(req.responseText);
			}

		}

	}

}

function doLogin() {
	var username = document.getElementById("username").value;
	var password = document.getElementById("password").value;

	var jsonData = {
		"username": username,
		"password": password
	}

	var req = new XMLHttpRequest();

	req.open("POST", "/rest/login/v1", true);
	req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	req.send(JSON.stringify(jsonData));

	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			if (req.status == 200) {
				alert("Successfull login.");
				localStorage.setItem("sessionUser", username);
				var urole = JSON.parse(req.responseText).role;
				localStorage.setItem("sessionRole", urole);
				switch (urole) {
					case "USER":
						window.location.href = "../pages/loggedIn.html";
						break;
					case "GBO":
						window.location.href = "../pages/loggedIn.html";
						break;
					case "GA":
						window.location.href = "../pages/loggedIn.html";
						break;
					case "GS":
						window.location.href = "../pages/loggedIn.html";
						break;
					case "SU":
						window.location.href = "../pages/loggedIn.html";
						break;
				}
			}
			else {
				alert(req.responseText);
			}
		}
	}
}


function doMainPage() {
	resetRightBox();
	var username = localStorage.getItem("sessionUser");
	var jsonData = {
		"username": username
	}
	var req = new XMLHttpRequest();

	req.open("POST", "/rest/listSelf/v1", true);
	req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	req.send(JSON.stringify(jsonData));

	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			if (req.status == 200) {
				document.getElementById("userFullNameDisplay").innerHTML = JSON.parse(req.responseText).fullName;
				document.getElementById("usernameDisplay").innerHTML = username;
				document.getElementById("emailDisplay").innerHTML = JSON.parse(req.responseText).email;
				document.getElementById("phoneNumberDisplay").innerHTML = JSON.parse(req.responseText).telephoneNumber;
				document.getElementById("userProfilePic").src = "https://storage.googleapis.com/lofty-flare-379310.appspot.com/" + localStorage.getItem("sessionUser");

				switch (localStorage.getItem("sessionRole")) {
					case "USER":
						document.getElementById("usernameDisplay").style.backgroundColor = "green";
						document.getElementById("maintenanceMode").style.display = "none";
						break;
					case "GBO":
						document.getElementById("usernameDisplay").style.backgroundColor = "yellow";
						document.getElementById("maintenanceMode").style.display = "none";
						break;
					case "GA":
						document.getElementById("usernameDisplay").style.backgroundColor = "blue";
						document.getElementById("maintenanceMode").style.display = "none";
						break;
					case "GS":
						document.getElementById("usernameDisplay").style.backgroundColor = "orange";
						document.getElementById("maintenanceMode").style.display = "inline";
						break;
					case "SU":
						document.getElementById("usernameDisplay").style.backgroundColor = "red";
						document.getElementById("maintenanceMode").style.display = "inline";
						break;

				}
			}
			else {
				alert(req.responseText);
			}
		}
	}
}

function resetRightBox() {
	document.getElementById("userProfilePicRB").style.display = "none";
	document.getElementById("onlyTxtData").style.display = "none";
	document.getElementById("targetUsernameTxt").style.display = "none";
	document.getElementById("targetUsername").style.display = "none";
	document.getElementById("removeBtn").style.display = "none";
	document.getElementById("modPwdBtn").style.display = "none";
	document.getElementById("oldPwdTxt").style.display = "none";
	document.getElementById("oldPwd").style.display = "none";
	document.getElementById("newPwdTxt").style.display = "none";
	document.getElementById("newPwd").style.display = "none";
	document.getElementById("newPwdConfTxt").style.display = "none";
	document.getElementById("newPwdConf").style.display = "none";
	document.getElementById("modPwdBtn").style.display = "none";
	document.getElementById("targetVisibilityTxt").style.display = "none";
	document.getElementById("targetVisibilitySel").style.display = "none";
	document.getElementById("targetFullNameTxt").style.display = "none";
	document.getElementById("targetFullName").style.display = "none";
	document.getElementById("targetEmailTxt").style.display = "none";
	document.getElementById("targetEmail").style.display = "none";
	document.getElementById("targetTelephoneTxt").style.display = "none";
	document.getElementById("targetTelephone").style.display = "none";
	document.getElementById("targetMobilePhoneTxt").style.display = "none";
	document.getElementById("targetMobilePhone").style.display = "none";
	document.getElementById("targetOccupationTxt").style.display = "none";
	document.getElementById("targetOccupation").style.display = "none";
	document.getElementById("targetWorkPlaceTxt").style.display = "none";
	document.getElementById("targetWorkPlace").style.display = "none";
	document.getElementById("targetAddressTxt").style.display = "none";
	document.getElementById("targetAddress").style.display = "none";
	document.getElementById("targetCompAddressTxt").style.display = "none";
	document.getElementById("targetCompAddress").style.display = "none";
	document.getElementById("targetZipTxt").style.display = "none";
	document.getElementById("targetZip").style.display = "none";
	document.getElementById("targetNIFTxt").style.display = "none";
	document.getElementById("targetNIF").style.display = "none";
	document.getElementById("targetRoleTxt").style.display = "none";
	document.getElementById("targetRole").style.display = "none";
	document.getElementById("targetStateTxt").style.display = "none";
	document.getElementById("targetStateSel").style.display = "none";
	document.getElementById("profilePicTxt").style.display = "none";
	document.getElementById("profilePic").style.display = "none";
	document.getElementById("modProfPicBtn").style.display = "none";
	document.getElementById("modAttBtn").style.display = "none";
}

function doProfile() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();

		var username = localStorage.getItem("sessionUser");
		var jsonData = {
			"username": username
		}
		var req = new XMLHttpRequest();

		req.open("POST", "/rest/listSelf/v1", true);
		req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		req.send(JSON.stringify(jsonData));

		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					var fullname = JSON.parse(req.responseText).fullName;
					var email = JSON.parse(req.responseText).email;
					var telephone = JSON.parse(req.responseText).telephoneNumber;
					var mobile = JSON.parse(req.responseText).mobilePhoneNumber;
					var visibility = JSON.parse(req.responseText).profileVisibility;
					var ocuppation = JSON.parse(req.responseText).occupation;
					var workplace = JSON.parse(req.responseText).workPlace;
					var address = JSON.parse(req.responseText).address;
					var compaddress = JSON.parse(req.responseText).compAddress;
					var zip = JSON.parse(req.responseText).zip;
					var nif = JSON.parse(req.responseText).nif;
					var role = JSON.parse(req.responseText).role;
					var state = JSON.parse(req.responseText).state;

					var thisTxt = fullname + "<p>" + email + "<p>" + telephone + "<p>" + mobile
						+ "<p>" + visibility + "<p>" + ocuppation + "<p>" + workplace + "<p>" + address
						+ "<p>" + compaddress + "<p>" + zip + "<p>" + nif + "<p>" + role + "<p>" + state;
					document.getElementById("onlyTxtData").innerHTML = thisTxt;
					document.getElementById("onlyTxtData").style.display = "inline";

					document.getElementById("userProfilePicRB").src = "https://storage.googleapis.com/lofty-flare-379310.appspot.com/" + localStorage.getItem("sessionUser");
					document.getElementById("userProfilePicRB").style.display = "inline";
				}
			}
		}
	}
}


function openRemove() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		document.getElementById("targetUsername").style.display = "inline";
		document.getElementById("targetUsernameTxt").style.display = "inline";
		document.getElementById("removeBtn").style.display = "inline";
	}
}

function doRemove() {
	var remover = localStorage.getItem("sessionUser");
	var toBeRemoved = document.getElementById("targetUsername").value;

	var jsonData = {
		"removerUsername": remover,
		"toBeRemovedUsername": toBeRemoved
	}

	var req = new XMLHttpRequest();

	req.open("POST", "/rest/removeUser/v1", true);
	req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	req.send(JSON.stringify(jsonData));

	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			alert(req.responseText);
		}
	}
	resetRightBox();
}

function openModifyPassword() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		document.getElementById("oldPwdTxt").style.display = "inline";
		document.getElementById("oldPwd").style.display = "inline";
		document.getElementById("newPwdTxt").style.display = "inline";
		document.getElementById("newPwd").style.display = "inline";
		document.getElementById("newPwdConfTxt").style.display = "inline";
		document.getElementById("newPwdConf").style.display = "inline";
		document.getElementById("modPwdBtn").style.display = "inline";
	}
}

function doModifyPassword() {
	var username = localStorage.getItem("sessionUser");
	var previousPassword = document.getElementById("oldPwd").value;
	var newPassword = document.getElementById("newPwd").value;
	var confirmationPassword = document.getElementById("newPwdConf").value;

	var jsonData = {
		"username": username,
		"previousPassword": previousPassword,
		"newPassword": newPassword,
		"confirmationPassword": confirmationPassword
	}

	var req = new XMLHttpRequest();

	req.open("POST", "/rest/modifyPassword/v1", true);
	req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	req.send(JSON.stringify(jsonData));

	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			alert(req.responseText);
		}
	}
	resetRightBox();
}

function doList() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		document.getElementById("onlyTxtData").style.display = "inline";
		var username = localStorage.getItem("sessionUser");

		var jsonData = {
			"username": username
		}

		var req = new XMLHttpRequest();

		req.open("POST", "/rest/listUser/v1", true);
		req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		req.send(JSON.stringify(jsonData));

		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					document.getElementById("onlyTxtData").innerHTML = req.responseText;
				}
				else {
					alert(req.responseText);
				}
			}
		}
	}
}

function doShowSessionToken() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		document.getElementById("onlyTxtData").style.display = "inline";
		var username = localStorage.getItem("sessionUser");

		var jsonData = {
			"username": username
		}

		var req = new XMLHttpRequest();

		req.open("POST", "/rest/showSessionToken/v1", true);
		req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		req.send(JSON.stringify(jsonData));

		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					document.getElementById("onlyTxtData").innerHTML = req.responseText;
				}
				else {
					alert(req.responseText);
				}
			}
		}
	}
}

function doLogout() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		var username = localStorage.getItem("sessionUser");

		var jsonData = {
			"username": username
		}

		var req = new XMLHttpRequest();

		req.open("POST", "/rest/logout/v1", true);
		req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		req.send(JSON.stringify(jsonData));

		req.onreadystatechange = function() {
			if (req.readyState == 4) {
				if (req.status == 200) {
					alert("You have logged out.")
					localStorage.removeItem("sessionUser");
					window.location.href = "../index.html";
				}
				else {
					alert(req.responseText);
				}
			}
		}
	}
}

function doMaintenanceMode() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		if (confirm("Are you sure you want to proceed?")) {
			var username = localStorage.getItem("sessionUser");

			var jsonData = {
				"username": username
			}

			var req = new XMLHttpRequest();

			req.open("POST", "/rest/maintenance/v1", true);
			req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
			req.send(JSON.stringify(jsonData));

			req.onreadystatechange = function() {
				if (req.readyState == 4) {
					alert(req.responseText);
				}
			}
		}
	}
}

function openModifyAttributes() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		document.getElementById("userProfilePicRB").style.display = "none";
		document.getElementById("onlyTxtData").style.display = "none";
		document.getElementById("targetUsernameTxt").style.display = "none";
		document.getElementById("targetUsername").style.display = "none";
		document.getElementById("removeBtn").style.display = "none";
		document.getElementById("modPwdBtn").style.display = "none";
		document.getElementById("oldPwdTxt").style.display = "none";
		document.getElementById("oldPwd").style.display = "none";
		document.getElementById("newPwdTxt").style.display = "none";
		document.getElementById("newPwd").style.display = "none";
		document.getElementById("newPwdConfTxt").style.display = "none";
		document.getElementById("newPwdConf").style.display = "none";
		document.getElementById("modPwdBtn").style.display = "none";
		document.getElementById("profilePicTxt").style.display = "none";
		document.getElementById("profilePic").style.display = "none";
		document.getElementById("modProfPicBtn").style.display = "none";
		document.getElementById("targetUsernameTxt").style.display = "inline";
		document.getElementById("targetUsername").style.display = "inline";
		document.getElementById("targetVisibilityTxt").style.display = "inline";
		document.getElementById("targetVisibilitySel").style.display = "inline";
		document.getElementById("targetFullNameTxt").style.display = "inline";
		document.getElementById("targetFullName").style.display = "inline";
		document.getElementById("targetEmailTxt").style.display = "inline";
		document.getElementById("targetEmail").style.display = "inline";
		document.getElementById("targetTelephoneTxt").style.display = "inline";
		document.getElementById("targetTelephone").style.display = "inline";
		document.getElementById("targetMobilePhoneTxt").style.display = "inline";
		document.getElementById("targetMobilePhone").style.display = "inline";
		document.getElementById("targetOccupationTxt").style.display = "inline";
		document.getElementById("targetOccupation").style.display = "inline";
		document.getElementById("targetWorkPlaceTxt").style.display = "inline";
		document.getElementById("targetWorkPlace").style.display = "inline";
		document.getElementById("targetAddressTxt").style.display = "inline";
		document.getElementById("targetAddress").style.display = "inline";
		document.getElementById("targetCompAddressTxt").style.display = "inline";
		document.getElementById("targetCompAddress").style.display = "inline";
		document.getElementById("targetZipTxt").style.display = "inline";
		document.getElementById("targetZip").style.display = "inline";
		document.getElementById("targetNIFTxt").style.display = "inline";
		document.getElementById("targetNIF").style.display = "inline";
		document.getElementById("targetRoleTxt").style.display = "inline";
		document.getElementById("targetRole").style.display = "inline";
		document.getElementById("targetStateTxt").style.display = "inline";
		document.getElementById("targetStateSel").style.display = "inline";
		document.getElementById("modAttBtn").style.display = "inline";
	}
}

function doModifyAttributes() {
	var tusername = document.getElementById("targetUsername").value;
	var tvisibility = document.getElementById("targetVisibilitySel").value;
	var tfullname = document.getElementById("targetFullName").value;
	var temail = document.getElementById("targetEmail").value;
	var ttelephone = document.getElementById("targetTelephone").value;
	var tmobile = document.getElementById("targetMobilePhone").value;
	var tocuppation = document.getElementById("targetOccupation").value;
	var tworkplace = document.getElementById("targetWorkPlace").value;
	var taddress = document.getElementById("targetAddress").value;
	var tcompaddress = document.getElementById("targetCompAddress").value;
	var tzip = document.getElementById("targetZip").value;
	var tnif = document.getElementById("targetNIF").value;
	var trole = document.getElementById("targetRole").value;
	var tstate = document.getElementById("targetStateSel").value;

	if (tusername == "") {
		tusername = null;
	}

	if (tvisibility == "") {
		tvisibility = null;
	}

	if (tfullname == "") {
		tfullname = null;
	}

	if (temail == "") {
		temail = null;
	}

	if (ttelephone == "") {
		ttelephone = null;
	}

	if (tmobile == "") {
		tmobile = null;
	}

	if (tocuppation == "") {
		tocuppation = null;
	}

	if (tworkplace == "") {
		tworkplace = null;
	}

	if (taddress == "") {
		taddress = null;
	}

	if (tcompaddress == "") {
		tcompaddress = null;
	}

	if (tzip == "") {
		tzip = null;
	}

	if (tnif == "") {
		tnif = null;
	}

	if (trole == "") {
		trole = null;
	}

	if (tstate == "") {
		tstate = null;
	}


	var jsonData = {
		"modifierUsername": localStorage.getItem("sessionUser"),
		"modifiedUsername": tusername,
		"profileVisibility": tvisibility,
		"fullName": tfullname,
		"email": temail,
		"telephoneNumber": ttelephone,
		"mobilePhoneNumber": tmobile,
		"occupation": tocuppation,
		"workPlace": tworkplace,
		"address": taddress,
		"compAddress": tcompaddress,
		"zip": tzip,
		"nif": tnif,
		"state": tstate,
		"role": trole
	}

	var req = new XMLHttpRequest();

	req.open("POST", "/rest/modifyAttributes/v1", true);
	req.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	req.send(JSON.stringify(jsonData));

	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			alert(req.responseText);
		}
	}
	resetRightBox();
}

function openProfilePic() {
	if (localStorage.getItem("sessionUser") == null) {
		alert("No one is logged in.");
		window.location.href = "../index.html";
	}
	else {
		resetRightBox();
		document.getElementById("profilePicTxt").style.display = "inline";
		document.getElementById("profilePic").style.display = "inline";
		document.getElementById("modProfPicBtn").style.display = "inline";
	}
}

function doUploadImage() {
	var req = new XMLHttpRequest();

	let fileInput = document.getElementById("profilePic");

	if (fileInput == null) {
		alert("No file selected");
	}
	else {
		let file = fileInput.files[0];
		req.open("POST", "/gcs/lofty-flare-379310.appspot.com/" + localStorage.getItem("sessionUser"), true);
		req.setRequestHeader("Content-Type", file.type);
		req.send(file);

		req.onreadystatechange = function() {
			if (req.readyState == 4)
				if (req.status == 200) {
					alert("File uploaded successfully");
				}
				else {
					alert("Upload failed. Try again later.")
				}
		}
	}
	resetRightBox();
	
	
	
	
}
