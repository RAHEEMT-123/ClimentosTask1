const API_URL = "/api/auth";


// SHOW LOGIN

function showLogin() {

    hideAllForms();

    document.getElementById("loginForm")
        .classList.remove("hidden");

    clearMessage();
}


// SHOW SIGNUP

function showSignup() {

    hideAllForms();

    document.getElementById("signupForm")
        .classList.remove("hidden");

    clearMessage();
}


// SHOW FORGOT

function showForgot() {

    hideAllForms();

    document.getElementById("forgotForm")
        .classList.remove("hidden");

    clearMessage();
}


// HIDE ALL FORMS

function hideAllForms() {

    document.getElementById("loginForm")
        .classList.add("hidden");

    document.getElementById("signupForm")
        .classList.add("hidden");

    document.getElementById("forgotForm")
        .classList.add("hidden");
}


// CLEAR MESSAGE

function clearMessage() {

    document.getElementById("message").innerText = "";

}


// SIGNUP

async function signup() {

    const name =
        document.getElementById("signupName").value.trim();

    const username =
        document.getElementById("signupUsername").value.trim();

    const email =
        document.getElementById("signupEmail").value.trim();

    const password =
        document.getElementById("signupPassword").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

    if (!name || !username || !email || !password ||
        !confirmPassword) {

        alert("Please fill all fields");

        return;
    }

    if (password !== confirmPassword) {

        alert("Passwords do not match");

        return;
    }

    try {

        const response = await fetch(API_URL + "/signup", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                name,
                username,
                email,
                password
            })

        });

        const result = await response.json();

        if (!response.ok) {

            throw new Error(
                result.message || "Signup failed"
            );

        }

        alert(result.message);

        document.getElementById("signupName").value = "";
        document.getElementById("signupUsername").value = "";
        document.getElementById("signupEmail").value = "";
        document.getElementById("signupPassword").value = "";
        document.getElementById("confirmPassword").value = "";

        showLogin();

    }

    catch(error) {

        alert(error.message);

    }

}


// LOGIN

async function login() {

    const username =
        document.getElementById("loginUsername").value.trim();

    const password =
        document.getElementById("loginPassword").value;

    if (!username || !password) {

        alert("Enter username and password");

        return;
    }

    try {

        const response = await fetch(API_URL + "/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            credentials: "same-origin",

            body: JSON.stringify({
                username,
                password
            })

        });

        const result = await response.json();

        if (!response.ok) {

            throw new Error(
                result.message || "Login failed"
            );

        }

        window.location.href = "home.html";

    }

    catch(error) {

        document.getElementById("message").innerText =
            error.message;

    }

}


// FORGOT PASSWORD

async function forgotPassword() {

    const email =
        document.getElementById("forgotEmail").value.trim();

    if (!email) {

        alert("Please enter registered email");

        return;
    }

    try {

        const response = await fetch(
            API_URL + "/forgot-password",
            {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    email
                })

            }
        );

        const result = await response.json();

        if (!response.ok) {

            throw new Error(
                result.message || "Email sending failed"
            );

        }

        alert(result.message);

        document.getElementById("forgotEmail").value = "";

        showLogin();

    }

    catch(error) {

        alert(error.message);

    }

}













