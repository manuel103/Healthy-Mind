import React from "react";
import { Card, Grid, Button } from "@material-ui/core";
import { TextField } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import "./Login.css";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "25ch",
    },
  },
}));

const Login = (props) => {
  const {
    email,
    setEmail,
    password,
    setPassword,
    handleLogin,
    handleSignup,
    hasAccount,
    setHasAccount,
    emailError,
    passwordError,
  } = props;

  //   const classes = styles;
  const classes = useStyles();

  return (
    <div className="signup flex flex-center w-100 h-100vh">
      <div className="p-8">
        <Card className="signup-card position-relative y-center">
          <Grid container>
            <Grid item lg={5} md={5} sm={5} xs={12}>
              <div className="p-32 flex flex-center flex-middle h-100">
                <img src="/assets/images/illustrations/dreamer.svg" alt="" />
              </div>
            </Grid>
            <Grid item lg={7} md={7} sm={7} xs={12}>
              <div className="p-36 h-100 bg-light-gray position-relative">
                <div className="loginContainer">
                  <form className={classes.root} noValidate autoComplete="off">
                    
                    {/* <TextField
                      id="outlined-name-input"
                      label="Name"
                      type="text"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      autoComplete="current-name"
                      variant="outlined"
                    /> */}
                    <br />
                    <TextField
                      id="outlined-email-input"
                      label="Email"
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      autoComplete="current-email"
                      variant="outlined"
                    />
                    <p className="errorMsg">{emailError}</p>
                    <TextField
                      id="outlined-password-input"
                      label="Password"
                      type="password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      autoComplete="current-password"
                      variant="outlined"
                    />
                    <p className="errorMsg">{passwordError}</p>
                    <div className="btnContainer">
                      {hasAccount ? (
                        <>
                          <Button
                            onClick={handleSignup}
                            variant="contained"
                            color="primary"
                          >
                            Sign Up
                          </Button>
                          <p>
                            Have an account?
                            <Button
                              className="capitalize"
                              onClick={() => setHasAccount(!hasAccount)}
                            >
                              Sign In
                            </Button>
                          </p>
                        </>
                      ) : (
                        <>
                          <Button
                            onClick={handleLogin}
                            variant="contained"
                            color="primary"
                          >
                            Sign In
                          </Button>

                          <p>
                            Don't have an account ?
                            <Button
                              className="capitalize"
                              onClick={() => setHasAccount(!hasAccount)}
                            >
                              Sign Up
                            </Button>
                          </p>
                        </>
                      )}
                    </div>
                  </form>
                </div>
              </div>
            </Grid>
          </Grid>
        </Card>
      </div>
    </div>
  );
};

export default Login;
