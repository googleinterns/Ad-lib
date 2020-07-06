import React, { Component } from "react";
//import { makeStyles } from "@material-ui/core/styles";
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

/*const useStyles = makeStyles(theme => ({
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
}));*/

// Create basic form component with 
export default class Form extends Component {
  state = {
    timeAvailableUntil: '',
    duration: '',
    role: '',
    productArea: '',
    years: '',
    ergs: '',
    similarityRating: '',
    savePreferences: ''
  };

  // Handle state changes to fields
  handleChange = input => event => {
    this.setState({ [input]: event.target.value });
    console.log(input, " : ", event.target.value);
  };

  render() {
    //const classes = useStyles();
    const { timeAvailableUntil, duration, role, productArea, years, ergs, similarityRating, savePreferences } = this.state;
    const values = { timeAvailableUntil, duration, role, productArea, years, ergs, similarityRating, savePreferences };
    return (
      <div>
        <div>
          <div>
            <p>I'm free until...</p>
            <TextField
              id="time"
              type="time"
              defaultValue="09:30"/>
          </div>
          <div>
            <p>I want to talk for...</p>
            <FormControl>
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
        <div>
          <h4>About me:</h4>
          <RoleDropdown handleChange={this.handleChange} values={values} />
          <ProductAreaDropdown handleChange={this.handleChange} values={values} />
          <YearDropdown handleChange={this.handleChange} values={values} />
          <ERGDropdown handleChange={this.handleChange} values={values} />
        </div>
        <div>
          <div>
            <p>Match me with a similar Googler</p>
            <Switch />
          </div>
        </div>
        <div>
          <Checkbox />
          <div>
            <Button variant="contained" color="primary">
              Match me!
            </Button>
          </div>
        </div>
      </div>
    )
  }
}