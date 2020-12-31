const express = require("express");
const app = express(); // create our app w/ express
const Firebase = require("firebase");
const morgan = require("morgan");
const bodyParser = require("body-parser"); // pull information from HTML POST (express4)
const methodOverride = require("method-override");
const multer = require("multer");
const fs = require("fs");
const authMiddleware = require("./auth-middleware");

app.use(function (req, res, next) {
  //allow cross origin requests
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Methods", "POST, PUT, OPTIONS, DELETE, GET");
  res.header("Access-Control-Max-Age", "3600");
  res.header(
    "Access-Control-Allow-Headers",
    "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
  );

  next();
});

Firebase.initializeApp({
  databaseURL: "https://dl4ddoa.firebaseio.com",
  serviceAccount: "./testapp.json",
});

const db = Firebase.database();
const patientsRef = db.ref("patients");
const doctorsRef = db.ref("doctors");

// const FirebaseRef = new Firebase("https://dl4ddoa.firebaseio.com");

// configuration

app.use(express.static(__dirname + "/public")); // set the static files location /public/img will be /img for users

app.use("/public/uploads", express.static(__dirname + "/public/uploads"));

app.use(morgan("dev")); // log every request to the console

app.use(bodyParser.urlencoded({ extended: "true" })); // parse application/x-www-form-urlencoded

app.use(bodyParser.json()); // parse application/json

app.use(bodyParser.json({ type: "application/vnd.api+json" })); // parse application/vnd.api+json as json

// app.use("/", authMiddleware);

// app.get("/", function (req, res) {
//   res.sendfile("./index.html");
// });

// create patient
app.post("/api/createPatient", function (req, res) {
  // const userEmail = req.body.user_email;

  const data = req.body;

  patientsRef.push(data, function (err) {
    if (err) {
      res.send(err);
    } else {
      // const key = Object.keys(snapshot.val())[0];

      // console.log(key);

      res.json({ message: "Success: New Patient Saved.", result: true });
    }
  });
});

// update patient
app.put("/api/updatePatient", function (req, res) {
  const uid = "-MLl_ERoxyFyBNkykkRx";

  const data = req.body;

  patientsRef.child(uid).update(data, function (err) {
    if (err) {
      res.send(err);
    } else {
      patientsRef.child("uid").once("value", function (snapshot) {
        if (snapshot.val() == null) {
          res.json({ message: "Error: No user found", result: false });
        } else {
          res.json({
            message: "successfully update data",
            result: true,
            data: snapshot.val(),
          });
        }
      });
    }
  });
});

// delete patient
app.delete("/api/removePatient", function (req, res) {
  const uid = "tr";

  patientsRef.child(uid).remove(function (err) {
    if (err) {
      res.send(err);
    } else {
      res.json({ message: "Success: Patient deleted.", result: true });
    }
  });
});

// get patients
app.get("/api/getPatients", function (req, res) {
  // const uid = "-MLl_ERoxyFyBNkykkRx";

  patientsRef.once("value", function (snapshot) {
    //console.log(snapshot);

    if (snapshot.val() == null) {
      res.json({ message: "Error: No user found", result: false });
    } else {
      const result = snapshot.val();
      res.json({
        // message: "successfully fetched data",
        // result: true,
        patients: Object.values(result),
      });
    }
  });
  //  }
});

app.get("/api/getDoctors", function (req, res) {
  // const uid = "-MLl_ERoxyFyBNkykkRx";

  doctorsRef.once("value", function (snapshot) {
    //console.log(snapshot);

    if (snapshot.val() == null) {
      res.json({ message: "Error: No user found", result: false });
    } else {
      const result = snapshot.val();
      res.json({
        // message: "successfully fetched data",
        // result: true,
        doctors: Object.values(result),
      });
    }
  });
  //  }
});

// Get patient depression results
app.get("/api/getdepressionResults", function (req, res) {

  const username = req.username

  patientsRef.child(username).once("value", function (snapshot) {
    if (snapshot.val() == null) {
      res.json({ message: "Error: No results found", result: false });
    } else {
      // snapshot.forEach(function (userSnapshot) {
      const result = snapshot.val();
      // console.log(result)
      res.json({
        // message: "successfully fetched data",
        // result: true,
        depression_results: Object.values(result),
      });
      // });
    }
  });

  // patientsRef.once("value", function (snapshot) {
  //   snapshot.forEach(function (userSnapshot) {
  //     var blogs = userSnapshot.val().depression_levels;
  //     console.log(blogs);
  //     res.json({
  //       depression: Object.values(blogs),
  //     });
  //     // var daBlog = blogs["depression_levels"];
  //     // console.log(daBlog)
  //   });
  // });
});

// login
app.post("/session/login", (req, res) => {
  // Get the ID token passed and the CSRF token.
  const idToken = req.body.idToken.toString();
  const csrfToken = req.body.csrfToken.toString();
  // Guard against CSRF attacks.
  if (csrfToken !== req.cookies.csrfToken) {
    res.status(401).send("UNAUTHORIZED REQUEST!");
    return;
  }
  // Set session expiration to 5 days.
  const expiresIn = 60 * 60 * 24 * 5 * 1000;
  // Create the session cookie. This will also verify the ID token in the process.
  // The session cookie will have the same claims as the ID token.
  // To only allow session cookie setting on recent sign-in, auth_time in ID token
  // can be checked to ensure user was recently signed in before creating a session cookie.
  admin
    .auth()
    .createSessionCookie(idToken, { expiresIn })
    .then(
      (sessionCookie) => {
        // Set cookie policy for session cookie.
        const options = { maxAge: expiresIn, httpOnly: true, secure: true };
        res.cookie("session", sessionCookie, options);
        res.end(JSON.stringify({ status: "success" }));
      },
      (error) => {
        res.status(401).send("UNAUTHORIZED REQUEST!");
      }
    );
});

app.listen(9000);

console.log("port is 9000");
