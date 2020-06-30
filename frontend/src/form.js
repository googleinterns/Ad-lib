import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import FormControl from "@material-ui/core/FormControl";
import NativeSelect from "@material-ui/core/NativeSelect";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Switch from './switch.js';
import Checkbox from './checkbox.js';
import ERGDropdown from './erg-dropdown.js';
import RoleDropdown from "./role-dropdown.js";
import YearDropdown from "./years-dropdown.js";
import ProductAreaDropdown from "./pa-dropdown.js";

const useStyles = makeStyles(theme => ({
  inputField: {
    margin: theme.spacing(2),
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
        <RoleDropdown className={classes.inputField}/>
        <ProductAreaDropdown className={classes.inputField}/>
        <YearDropdown className={classes.inputField}/>
        <ERGDropdown className={classes.inputField}/>
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