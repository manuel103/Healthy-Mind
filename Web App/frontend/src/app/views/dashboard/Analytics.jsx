import React, { Component, Fragment } from "react";
import {
  Grid,
  Card
} from "@material-ui/core";

import DoughnutChart from "../charts/echarts/Doughnut";
import TableCard from "./shared/DepressionCard";
import StatCards2 from "./shared/StatCards2";
import { withStyles } from "@material-ui/styles";
import './shared/modal.css'

class Dashboard1 extends Component {
  state = {};

  render() {
    let { theme } = this.props;

    return (
      <Fragment>
        <div className="pb-86 pt-30 px-30 bg-primary"></div>

        <div className="analytics m-sm-30 mt--72">
          <Grid container spacing={3}>
            <Grid item lg={8} md={8} sm={12} xs={12}>
              <StatCards2 />

              <TableCard />
            </Grid>

            <Grid item lg={4} md={4} sm={12} xs={12}>
              <Card className="px-24 py-30 mb-16">
                <div className="card-title ModalTitle">
                  General Prediction Results
                </div>
                <div className="card-subtitle">All Time</div>
                <DoughnutChart
                  height="400px"
                  color={[
                    "#036916",
                    // theme.palette.primary.main,
                    "#f5c000",
                    "#f56200",
                    "#f53900",
                    "#f50039",
                    "#ed2d2d",
                    "#b80000",
                  ]}
                />
              </Card>
            </Grid>
          </Grid>
        </div>
      </Fragment>
    );
  }
}

export default withStyles({}, { withTheme: true })(Dashboard1);
