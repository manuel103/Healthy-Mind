import React, { useState, useEffect } from "react";
import { Card } from "@material-ui/core";
import BootstrapTable from "react-bootstrap-table-next";
import { Modal, Button } from "react-bootstrap";
import "./modal.css";
import { Link } from "react-router-dom";
import StorageIcon from "@material-ui/icons/Storage";
import NotInterestedIcon from "@material-ui/icons/NotInterested";
import RadioButtonCheckedIcon from "@material-ui/icons/RadioButtonChecked";
import ErrorIcon from "@material-ui/icons/Error";
import ErrorOutlineIcon from "@material-ui/icons/ErrorOutline";
import PanoramaFishEyeIcon from "@material-ui/icons/PanoramaFishEye";
import ModifiedAreaChart from "./ModifiedAreaChart";
import { fireDb } from "../../../services/firebase/firebaseConfig";

const TableCard = () => {
  // ************************Added features**********************************
  const [patients, setPatients] = useState([]);
  const [modalInfo, setModalInfo] = useState([]);
  const [depressionLevels, setdepressionLevels] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const [user, setUser] = useState("");
  let docPatients = [];

  // const [docPatients, setDocPatients] = useState([]);

  // Prediction count variables
  let none_count = 0;
  let normal_count = 0;
  let mild_count = 0;
  let moderate_count = 0;
  let moderatelySevere_count = 0;
  let severe_count = 0;
  let extreme_count = 0;

  // Prediction mapping variables
  let normal = "";
  let none = "";
  let mild = "";
  let severe = "";
  let moderately_severe = "";
  let moderate = "";
  let extreme = "";
  let empty_result = "";

  // Prediction score variables
  let none_score = "";
  let mild_score = "";
  let severe_score = "";
  let moderately_severe_score = "";
  let moderate_score = "";

  const authListener = () => {
    fireDb.auth().onAuthStateChanged((user) => {
      // clearInputs();
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

  // API Call
  useEffect(() => {
    async function loadPatients() {
      //fetch the patient list
      const request = await fetch(
        "https://healthymind.herokuapp.com/api/getPatients",
        {
          //use the authorization https://www.youtube.com/watch?v=T3Px88x_PsA http://localhost:3000/#/app/main/dashboard
          // headers: {
          //   Authorization: "Bearer " + localStorage.getItem("@token"),
          // },
        }
      );

      const allPatients = await request.json();

      //set the patient list on state
      setPatients(allPatients.patients);
    }

    //invoke the function
    loadPatients();
  }, []);

  patients &&
    Object.keys(patients).forEach(function (key) {
      const data = patients[key];
      // for (element in data) {
      if (data.docId === user.email) {
        // console.log(data.docId);
        docPatients.push(data);
      }

      // }
    });

  // console.log(docPatients);

  // console.log("docPatientss",[docPatients])

  // Handling @row's data on table click
  const rowEvents = {
    onClick: (e, row) => {
      console.log(
        row.depression_levels === undefined ? "No data" : row.depression_levels
      );
      setModalInfo(row);
      setdepressionLevels(row.depression_levels);
      toggleTrueFalse();
    },
  };

  // Calculate the average counts for @ prediction variable
  depressionLevels &&
    Object.keys(depressionLevels).forEach(function (key) {
      const depression_levels = depressionLevels[key];
      if (depression_levels === "none") {
        none_count += 1;
      } else if (depression_levels === "mild") {
        mild_count += 1;
      } else if (depression_levels === "severe") {
        severe_count += 1;
      } else if (depression_levels === "moderately severe") {
        moderatelySevere_count += 1;
      } else if (depression_levels === "moderate") {
        moderate_count += 1;
      } else {
        // no_prediction = "No Predictions Made";
      }
      // console.log("Total none:", none_count);
    });

  // Variables to get the mod (Most frequent depression prediction)
  const all_scores = [
    none_count,
    mild_count,
    severe_count,
    moderatelySevere_count,
    moderate_count,
  ];

  // Get total number of samples analyzed
  const total_scores =
    none_count +
    normal_count +
    mild_count +
    severe_count +
    moderatelySevere_count +
    moderate_count +
    extreme_count;

  // convert scores to percentage
  const none_count_P = Math.round((none_count / total_scores) * 100);
  const mild_count_P = Math.round((mild_count / total_scores) * 100);
  const severe_count_P = Math.round((severe_count / total_scores) * 100);
  const moderatelySevere_count_P = Math.round(
    (moderatelySevere_count / total_scores) * 100
  );
  const moderate_count_P = Math.round((moderate_count / total_scores) * 100);

  const percentage_scores = [
    none_count_P,
    mild_count_P,
    moderate_count_P,
    moderatelySevere_count_P,
    severe_count_P,
  ];

  // console.log("Percentages...", percentage_scores);
  // Returning the mod value for rendering & assign scores to the depression level
  const mod = Math.max(...all_scores);
  if (none_count === mod) {
    none = "none";
    none_score = 0;
  } else if (mild_count === mod) {
    mild = "mild";
    mild_score = Math.floor(Math.random() * (5 - 0 + 1) + 0);
  } else if (severe_count === mod) {
    severe = "severe";
    severe_score = Math.floor(Math.random() * (25 - 15 + 1) + 15);
  } else if (moderatelySevere_count === mod) {
    moderately_severe = "moderately severe";
    moderately_severe_score = Math.floor(Math.random() * (15 - 10 + 1) + 10);
  } else if (moderate_count === mod) {
    moderate = "moderate";
    moderate_score = Math.floor(Math.random() * (10 - 5 + 1) + 5);
  } else {
    // no_prediction = "No Predictions Made";
  }

  // Check if the prediction array is empty
  if (
    none === "" &&
    normal === "" &&
    mild === "" &&
    severe === "" &&
    moderately_severe === "" &&
    moderate === "" &&
    extreme === ""
  ) {
    empty_result = "No predictions made";
  }

  // Bootsrap table with patient summary details
  const columns = [
    {
      dataField: "name",
      text: "name",
      style: { cursor: "pointer" },
    },
    {
      dataField: "email",
      text: "email",
      style: { cursor: "pointer" },
    },
    {
      dataField: "profile.general.age",
      text: "age",
      style: { cursor: "pointer" },
    },
  ];

  const toggleTrueFalse = () => {
    setShowModal(handleShow);
  };

  // Patient modal
  const ModalContent = () => {
    return (
      <Modal
        show={show}
        onHide={handleClose}
        dialogClassName="patientDetails-modal"
      >
        <Modal.Header closeButton>
          <Modal.Title>{modalInfo.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="container">
            <div className="main-body">
              {/* Breadcrumb */}
              <nav aria-label="breadcrumb" className="main-breadcrumb">
                <ol className="breadcrumb">
                  <li className="breadcrumb-item">
                    <Link to="/">Home</Link>
                  </li>
                  <li className="breadcrumb-item">
                    <Link to="/">Patients</Link>
                  </li>
                  <li className="breadcrumb-item active" aria-current="page">
                    {modalInfo.username}
                  </li>
                </ol>
              </nav>
              {/* /Breadcrumb */}
              <div className="row gutters-sm">
                <div className="col-md-4 mb-3">
                  <div className="card">
                    <div className="card-body">
                      <div className="d-flex flex-column align-items-center text-center">
                        <img
                          src="/assets/images/avatar.png"
                          alt="Admin"
                          className="rounded-circle"
                          width={150}
                        />
                        <div className="mt-3">
                          <h4 className="ModalTitle">
                            {/* {Object.keys(depressionLevels).map((key, i) => (
                              <p key={i}>
                                <span>Key Name: {key}</span>
                                <span>Value: {depressionLevels[key]}</span>
                              </p>
                            ))} */}
                            {modalInfo.username},{" "}
                            {modalInfo.profile?.general?.age}
                          </h4>
                          <p className="TableData mb-1">
                            {modalInfo.profile?.general?.education}
                          </p>
                          <p className="text-muted font-size-sm TableData">
                            {modalInfo.profile?.general?.employment}
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div className="card mt-3">
                    <ul className="list-group list-group-flush">
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0 ModalTitle">
                          <StorageIcon className="sideIcon" />
                          Number of Samples Analyzed
                        </h6>
                        <span className="DepressionScore">{total_scores}</span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0 ModalTitle">
                          <NotInterestedIcon className="sideIcon" />
                          Total None Predictions
                        </h6>
                        <span className="DepressionScore">{none_count}</span>
                      </li>

                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0 ModalTitle">
                          <PanoramaFishEyeIcon className="sideIcon" />
                          Total Mild Predictions
                        </h6>
                        <span className="DepressionScore">{mild_count}</span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0 ModalTitle">
                          <RadioButtonCheckedIcon className="sideIcon" />
                          Total Moderate Predictions
                        </h6>
                        <span className="DepressionScore">
                          {moderate_count}
                        </span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0 ModalTitle">
                          <ErrorOutlineIcon className="sideIcon" />
                          Total Moderately Severe Predictions
                        </h6>
                        <span className="DepressionScore">
                          {moderatelySevere_count}
                        </span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0 ModalTitle">
                          <ErrorIcon className="sideIcon" />
                          Total Severe Predictions
                        </h6>
                        <span className="DepressionScore">{severe_count}</span>
                      </li>
                    </ul>
                  </div>
                  <div className="card mt-3">
                    <div className="card h-100">
                      <div className="card-body">
                        <h6 className="d-flex align-items-center mb-3 ModalTitle">
                          {/* <i className="material-icons text-info mr-2">
                            assignment
                          </i> */}
                          Depression Scale
                        </h6>

                        <small>None (0)</small>
                        <div className="progress mb-3" style={{ height: 5 }}>
                          <div
                            className="progress-bar bg-primary"
                            role="progressbar"
                            style={{ width: "0-5%" }}
                            aria-valuenow={80}
                            aria-valuemin={0}
                            aria-valuemax={100}
                          />
                        </div>
                        <small>Mild (0-5)</small>
                        <div className="progress mb-3" style={{ height: 5 }}>
                          <div
                            className="progress-bar bg-primary"
                            role="progressbar"
                            style={{ width: "20%" }}
                            aria-valuenow={89}
                            aria-valuemin={0}
                            aria-valuemax={100}
                          />
                        </div>
                        <small>Moderate (5-10)</small>
                        <div className="progress mb-3" style={{ height: 5 }}>
                          <div
                            className="progress-bar bg-primary"
                            role="progressbar"
                            style={{ width: "70%" }}
                            aria-valuenow={66}
                            aria-valuemin={0}
                            aria-valuemax={100}
                          />
                        </div>
                        <small>Moderately Severe (10-15)</small>
                        <div className="progress mb-3" style={{ height: 5 }}>
                          <div
                            className="progress-bar bg-primary"
                            role="progressbar"
                            style={{ width: "80%" }}
                            aria-valuenow={66}
                            aria-valuemin={0}
                            aria-valuemax={100}
                          />
                        </div>
                        <small>Severe (15-25)</small>
                        <div className="progress mb-3" style={{ height: 5 }}>
                          <div
                            className="progress-bar bg-primary"
                            role="progressbar"
                            style={{ width: "100%" }}
                            aria-valuenow={55}
                            aria-valuemin={0}
                            aria-valuemax={100}
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="col-md-8">
                  <div className="card mb-3">
                    <div className="card-body">
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">Full Name</h6>
                        </div>
                        <div className="card-title col-sm-9 ModalTitle">
                          {modalInfo.name}
                        </div>
                      </div>
                      <hr />
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">Email</h6>
                        </div>
                        <div className="col-sm-9 TableData">
                          {modalInfo.email}
                        </div>
                      </div>
                      <hr />
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">Phone</h6>
                        </div>
                        <div className="col-sm-9 TableData">
                          {modalInfo.phoneNo}
                        </div>
                      </div>
                      <hr />
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">Trusted Contacts</h6>
                        </div>
                        <div className="col-sm-9 TableData">
                          {modalInfo.profile?.contacts?.contacts ? (
                            modalInfo.profile?.contacts?.contacts
                          ) : (
                            <p className="text-error">
                              Contact Details Not Provided
                            </p>
                          )}
                        </div>
                      </div>
                      <hr />
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">
                            Notify Trusted Contacts of Patient's Progress?
                          </h6>
                        </div>
                        <div className="col-sm-9 TableData">
                          {modalInfo.profile?.contacts?.notify ? (
                            modalInfo.profile?.contacts?.notify
                          ) : (
                            <p className="text-error">Response Not Provided</p>
                          )}
                        </div>
                      </div>
                      <hr />
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">
                            Predicted Depression Level
                          </h6>
                        </div>
                        <div className="col-sm-9 TableData">
                          {/* Render the mod prediction value */}
                          {none}
                          {mild}
                          {moderate}
                          {moderately_severe}
                          {severe}
                          {empty_result}
                        </div>
                      </div>
                      <hr />
                      <div className="row">
                        <div className="col-sm-3">
                          <h6 className="mb-0 ModalTitle">Depression Score</h6>
                        </div>
                        <div className="col-sm-9 TableData">
                          {none_score}
                          {mild_score}
                          {moderate_score}
                          {moderately_severe_score}
                          {severe_score}
                        </div>
                      </div>
                    </div>
                  </div>
                  <h6 className="ModalTitle">Linear Trend</h6>
                  <div className="card mb-3 bg-primary">
                    <div className="card-body">
                      <ModifiedAreaChart
                        height="280px"
                        option={{
                          series: [
                            {
                              // data: [34, 45, 31, 45, 31, 43],

                              data: percentage_scores,
                              type: "line",
                            },
                          ],
                          xAxis: {
                            data: [
                              "None",
                              "Mild",
                              "Moderate",
                              "Mod. Severe",
                              "Severe",
                            ],
                          },
                        }}
                      ></ModifiedAreaChart>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    );
  };
  return (
    <Card elevation={3} className="pt-20 mb-24">
      <div className="card-title px-24 mb-12">patients list</div>
      <div className="overflow-auto">
        <BootstrapTable
          striped
          hover
          // pagination={paginationFactory()}
          keyField="name"
          loading={true}
          data={
            docPatients === undefined ? (docPatients = "No data") : docPatients
          }
          columns={columns}
          rowEvents={rowEvents}
        />
        {show ? <ModalContent /> : null}
      </div>
    </Card>
  );
};

export default TableCard;
