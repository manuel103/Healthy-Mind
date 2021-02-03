import React, { useState, useEffect } from "react";
import ProfileForm from "./ProfileForm";
import firebaseDb from "../../services/firebase/firebaseConfig";
import { fireDb } from "../../services/firebase/firebaseConfig";
import FetchDoctor from './FetchDoctor'

const Profile = () => {
  const [user, setUser] = useState("");
  const [doctor, setDoctor] = useState(0);
  var [currentId, setCurrentId] = useState("");

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

  useEffect(() => {
    firebaseDb
      .child("doctors")
      .on("value", (snapshot) => {
        if (snapshot.val() != null) {
          setDoctor({
            ...snapshot.val(),
          });

          // snapshot.forEach(function (childSnapshot) {
          //   var childData = childSnapshot.val();
          //   var id = childData.id;
          //   console.log(childData);
          // });
        }
      });
  }, []);

  const addOrEdit = (obj) => {
    firebaseDb
      .child("doctors")
      .child(user.email.split(".").join(""))
      .set(obj, (err) => {
        if (err) {
          console.log(err);
        }
      });
  };

  const onDelete = (id) => {
    // record with given id is to be deleted.
  };

  return (
    <>
    <FetchDoctor/>
      <div className="row">
        <div className="col-md-5">
          <ProfileForm addOrEdit={addOrEdit} />
        </div>
      </div>
      <div className="col-md-7">
        {/* <table className="table table-borderless table-stripped">
          <thead className="thead-light">
            <tr>
              <th>Name</th>
              <th>Mobile</th>
              <th>Email</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {Object.keys(doctor).map((key) => (
              <tr key={key}>
                <td>{doctor[key].fullName}</td>
                <td>{doctor[key].mobile}</td>
                <td>{doctor[key].email}</td>
                <td className="bg-light">
                  <a
                    className="btn text-primary"
                    onClick={() => {
                      setCurrentId(key);
                    }}
                  >
                    <i className="fas fa-pencil-alt"></i>
                  </a>
                  <a
                    className="btn text-danger"
                    onClick={() => {
                      onDelete(key);
                    }}
                  >
                    <i className="far fa-trash-alt"></i>
                  </a>
                </td>
              </tr>
            ))}
          </tbody>
        </table> */}
      </div>
    </>
  );
};

export default Profile;
