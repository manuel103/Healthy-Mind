import React, { useState, useEffect } from "react";
import { fireDb } from "../../services/firebase/firebaseConfig";
import { ValidatorForm, TextValidator } from "react-material-ui-form-validator";
import { Button, Icon, Grid } from "@material-ui/core";

const ProfileForm = (props) => {
  const [user, setUser] = useState("");

  const authListener = () => {
    fireDb.auth().onAuthStateChanged((user) => {
      if (user) {
        setUser(user);
      } else {
        setUser("");
      }
    });
  };

  useEffect(() => {
    authListener();
  }, []);

  const initialFieldValues = {
    fullName: "",
    mobile: "",
    age: "",
  };

  var [values, setValues] = useState(initialFieldValues);

  //   useEffect(() => {
  //     if (props.currentId == "") setValues({ ...initialFieldValues });
  //     else
  //       setValues({
  //         ...props.contactObjects[props.currentId],
  //       });
  //   }, [props.currentId, props.contactObjects]);

  const handleInputChange = (e) => {
    var { name, value } = e.target;
    setValues({
      ...values,
      [name]: value,
      email: user.email,
    });
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    props.addOrEdit(values);
  };

  return (
    <div className="pb-86 pt-30 px-30">
      <React.Fragment>
        <ValidatorForm
          autoComplete="off"
          onSubmit={handleFormSubmit}
          onError={(errors) => null}
        >
          <Grid container spacing={6}>
            <Grid item lg={6} md={6} sm={12} xs={12}>
              <TextValidator
                className="mb-16 w-100"
                label="Full Name"
                onChange={handleInputChange}
                type="text"
                name="fullName"
                value={values.fullName}
                validators={["required"]}
                errorMessages={["this field is required"]}
              />
              <TextValidator
                className="mb-16 w-100"
                label="Age"
                onChange={handleInputChange}
                type="number"
                name="age"
                value={values.age}
                validators={["required"]}
                errorMessages={["this field is required"]}
              />
              <TextValidator
                className="mb-16 w-100"
                label="Phone Number"
                onChange={handleInputChange}
                type="number"
                name="mobile"
                value={values.mobile}
                validators={["required"]}
                errorMessages={["this field is required"]}
              />
              <label>Email</label>
              <TextValidator
                className="mb-16 w-100"
                type="text"
                value={user.email}
                disabled="disabled"
              />
              <label>Id</label>
              <TextValidator
                className="mb-16 w-100"
                type="text"
                value={user.email}
                disabled="disabled"
              />
              <Button color="primary" variant="contained" type="submit">
                <Icon>send</Icon>
                <span className="pl-8 capitalize">Update</span>
              </Button>
            </Grid>
          </Grid>
        </ValidatorForm>
      </React.Fragment>
    </div>
  );
};

export default ProfileForm;
