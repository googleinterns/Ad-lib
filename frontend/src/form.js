import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import FormControl from "@material-ui/core/FormControl";
import NativeSelect from "@material-ui/core/NativeSelect";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Switch from './switch.js'
import Checkbox from './checkbox.js';

const useStyles = makeStyles(theme => ({
  inputField: {
    marginLeft: theme.spacing(2),
    marginBottom: theme.spacing(2),
    width: 180
  },
  divStyle: {
    display: 'flex',
    alignItems: 'flex-end',
    justifyContent: 'space-between',
    margin: theme.spacing(2)
  },
  section: {
    margin: theme.spacing(2),
    width: 480
  },
  about: {
    margin: theme.spacing(4),
    width: 480
  }
}));

// Create basic form component with two fields and submit button
export default function Form() {
  const classes = useStyles();

  return (
    <div>
      <div className={classes.section}>
        <div className={classes.divStyle}>
          <p>I'm free until...</p>
          <TextField className={classes.inputField}
            id="time"
            type="time"
            defaultValue="09:30"/>
        </div>
        <div className={classes.divStyle}>
          <p>I want to talk for...</p>
          <FormControl className={classes.inputField}>
            <NativeSelect
              id="duration-input"
              name="duration"
              inputProps={{ "aria-label": "duration" }}
            >
              <option value={15}>15 minutes</option>
              <option value={30}>30 minutes</option>
              <option value={45}>45 minutes</option>
              <option value={60}>60 minutes</option>
            </NativeSelect>
          </FormControl>
        </div>
      </div>  
      <div className={classes.about}>
        <h4>About me:</h4>
        <div className={classes.divStyle}>
          <p>My role is... </p>
          <FormControl className={classes.inputField}>
            <NativeSelect
              id="role-input"
              name="role"
              inputProps={{ "aria-label": "role" }}
            >
              <option value={"Software Engineer"}>Software Engineer</option>
              <option value={"Product Manager"}>Product Manager</option>
              <option value={"UI/UX Designer"}>UI/UX Designer</option>
              <option value={"Intern"}>Intern</option>
            </NativeSelect>
          </FormControl>
        </div>
        <div className={classes.divStyle}>
          <p>My product area is...</p>
          <FormControl className={classes.inputField}>
            <NativeSelect
              id="product-area-input"
              name="product-area"
              inputProps={{ "aria-label": "product-area" }}
            >
              <option value={"Google Play"}>Google Play</option>
              <option value={"Google Search"}>Google Search</option>
              <option value={"Google Maps"}>Google Maps</option>
              <option value={"Youtube"}>Youtube</option>
            </NativeSelect>
          </FormControl>
        </div>
        <div className={classes.divStyle}>
          <p>I have been at Google for...</p>
          <FormControl className={classes.inputField}>
            <NativeSelect
              id="years-input"
              name="years"
              inputProps={{ "aria-label": "years" }}
            >
              <option value={"0-5"}>0-5 years</option>
              <option value={"6-10"}>6-10 years</option>
              <option value={"11-15"}>11-15 years</option>
              <option value={"16-20"}>16-20 years</option>
              <option value={"20+"}>20+ years</option>
            </NativeSelect>
          </FormControl>
        </div>
        <div className={classes.divStyle}>
          <p>I am part of these ERGs:</p>
          <FormControl className={classes.inputField}>
            <NativeSelect
              id="erg-input"
              name="erg"
              inputProps={{ "aria-label": "erg" }}
            >
              <option value={"women@"}>Women @ Google</option>
              <option value={"greyglers"}>Greyglers</option>
              <option value={"bgn"}>BGN</option>
              <option value={"agn"}>AGN</option>
              <option value={"hola"}>Hola</option>
            </NativeSelect>
          </FormControl>
        </div>
      </div>
      <div className={classes.section}>
        <div className={classes.divStyle}>
          <p>Match me with a similar Googler</p>
          <Switch className={classes.inputField}/>
        </div>
      </div>
      <div>
        <Checkbox className={classes.inputField}/>
        <div className={classes.divStyle}>
          <Button variant="contained" color="primary">
            Submit
          </Button>
        </div>
      </div>
    </div>
  );
}