import React, { useState, useEffect } from "react";
import FirebaseConfig from "../../../app/services/firebase/firebaseConfig";
import Login from "./Login";
import history from "history.js";
import Analytics from "../dashboard/Analytics";
import { fireDb } from "../../services/firebase/firebaseConfig";


function SignIn() {
  const [name, setName] = useState("");
  const [user, setUser] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [emailError, setEmailError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [hasAccount, setHasAccount] = useState(false);

  const clearInputs = () => {
    setEmail("");
    setPassword("");
  };

  const clearErrors = () => {
    setEmailError("");
    setPasswordError("");
  };

  const handleLogin = () => {
    clearErrors();
    fireDb
      .auth()
      .signInWithEmailAndPassword(email, password)
      .catch((err) => {
        switch (err.code) {
          case "auth/invalid-email":
          case "auth/user-disabled":
          case "auth/user-not-found":
            setEmailError(err.message);
            break;
          case "auth/wrong-password":
            setPasswordError(err.message);
            break;
          default:
        }
      });
  };

  const handleSignup = () => {
    clearErrors();
    fireDb
      .auth()
      .createUserWithEmailAndPassword(email, password)
      .then((result) => {
        // Update the nickname
        result.user.updateProfile({
          displayName: user.name,
        });
      })

      .catch((err) => {
        switch (err.code) {
          case "auth/email-already-in-use":
          case "auth/invalid-email":
            setEmailError(err.message);
            break;
          case "auth/weak-password":
            setPasswordError(err.message);
            break;
          default:
        }
      });
  };

  const authListener = () => {
    fireDb.auth().onAuthStateChanged((user) => {
      clearInputs();
      if (user) {
        setUser(user);
        history.push({
          pathname: "/dashboard/patients",
        });
      } else {
        setUser("");
      }
    });
  };

  useEffect(() => {
    authListener();
  }, []);

  const handleChange = (event) => {
    event.persist();
    this.setState({
      [event.target.name]: event.target.value,
    });
  };

  const handleClick = () => {
    // history.push("/home");
    history.push({
      pathname: "/dashboard/patients",
    });
  };

  //   const Analytics = MatxLoadable({
  //   loader: () => import("./Analytics")
  // })

  return (
    <div>
      {/* <Router> */}
      {user ? (
        <>
          {/* <Route path="/dashboard/patients"> */}
          <Analytics />
          {/* {handleClick} */}
          {/* </Route> */}
        </>
      ) : (
        <Login
          name={name}
          setName={setName}
          email={email}
          setEmail={setEmail}
          password={password}
          setPassword={setPassword}
          handleLogin={handleLogin}
          handleSignup={handleSignup}
          hasAccount={hasAccount}
          setHasAccount={setHasAccount}
          emailError={emailError}
          passwordError={passwordError}
        />
      )}
      {/* </Router> */}
    </div>
  );
}

export default SignIn;
