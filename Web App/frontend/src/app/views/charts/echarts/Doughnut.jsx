import React, { useEffect, useState } from "react";
import ReactEcharts from "echarts-for-react";
import { withStyles } from "@material-ui/styles";
// import axios from "../../../../axios";

const DoughnutChart = ({ height, color = [], theme }) => {
  const [patients, setPatients] = useState([]);

  useEffect(() => {
    async function loadPatients() {
      //fetch the patient list
      const request = await fetch("http://localhost:9000/api/getPatients", {});
      // const request = await axios.get("/api/getPatients");

      const allPatients = await request.json();

      //set the patient list on state
      setPatients(allPatients?.patients);

      // console.log(allPatients.patients.length)
    }

    //invoke the function
    loadPatients();
  }, []);

  const P_depression = patients;
  let none_count = 0;
  let normal_count = 0;
  let mild_count = 0;
  let moderate_count = 0;
  let moderatelySevere_count = 0;
  let severe_count = 0;
  let extreme_count = 0;

  
  P_depression.forEach(function (item) {
    item.depression_levels && Object.keys(item.depression_levels).forEach(function (key) {
      const depression_levels = item.depression_levels[key];
      if (depression_levels == "none") {
        none_count += 1;
      } else if (depression_levels == "normal") {
        normal_count += 1;
      } else if (depression_levels == "mild") {
        mild_count += 1;
      } else if (depression_levels == "severe") {
        severe_count += 1;
      } else if (depression_levels == "moderately severe") {
        moderatelySevere_count += 1;
      } else if (depression_levels == "moderate") {
        moderate_count += 1;
      } else {
        extreme_count += 1;
      }
    });
  });

  // console.log(P_depression);

  const option = {
    legend: {
      show: true,
      itemGap: 20,
      icon: "circle",
      bottom: 0,
      textStyle: {
        color: theme.palette.text.secondary,
        fontSize: 13,
        fontFamily: "roboto",
      },
    },
    tooltip: {
      show: false,
      trigger: "item",
      formatter: "{a} <br/>{b}: {c} ({d}%)",
    },
    xAxis: [
      {
        axisLine: {
          show: false,
        },
        splitLine: {
          show: false,
        },
      },
    ],
    yAxis: [
      {
        axisLine: {
          show: false,
        },
        splitLine: {
          show: false,
        },
      },
    ],

    series: [
      {
        name: "Traffic Rate",
        type: "pie",
        radius: ["45%", "72.55%"],
        center: ["50%", "50%"],
        avoidLabelOverlap: false,
        hoverOffset: 5,
        stillShowZeroSum: false,
        label: {
          normal: {
            show: false,
            position: "center", // shows the description data to center, turn off to show in right side
            textStyle: {
              color: theme.palette.text.secondary,
              fontSize: 13,
              fontFamily: "roboto",
            },
            formatter: "{a}",
          },
          emphasis: {
            show: true,
            textStyle: {
              fontSize: "14",
              fontWeight: "normal",
              // color: "rgba(15, 21, 77, 1)"
            },
            formatter: "{b} \n{c} ({d}%)",
          },
        },
        labelLine: {
          normal: {
            show: false,
          },
        },
        data: [
          {
            value: none_count,
            name: "None",
          },
          {
            value: normal_count,
            name: "Normal",
          },
          {
            value: mild_count,
            name: "Mild",
          },
          {
            value: moderate_count,
            name: "Moderate",
          },
          {
            value: moderatelySevere_count,
            name: "Moderately Severe",
          },
          {
            value: severe_count,
            name: "Severe",
          },
          {
            value: extreme_count,
            name: "Extreme",
          },
        ],
        itemStyle: {
          emphasis: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  return (
    <ReactEcharts
      style={{ height: height }}
      option={{
        ...option,
        color: [...color],
      }}
    />
  );
};

export default withStyles({}, { withTheme: true })(DoughnutChart);
