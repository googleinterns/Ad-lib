import React, { Component } from 'react';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';

const productAreas = [
  'Play',
  'Search',
  'Ads',
  'Youtube',
  'Maps'
];

export default class ProductAreaDropdown extends Component {
  render() {
    const { values, handleChange } = this.props; 

    return (
      <div>
        <FormControl style={{width: 180}}>
          <InputLabel id="productArea">Product Area</InputLabel>
          <Select
            id="productArea-input"
            name="productArea"
            defaultValue={values.productArea}
            onChange={handleChange('productArea')}
            inputProps={{ "aria-label": "productArea" }}
          >
            {productAreas.map((currentProductArea) => (
              <MenuItem key={currentProductArea} value={currentProductArea}>
                <ListItemText primary={currentProductArea} />
              </MenuItem>
            ))}  
          </Select>
        </FormControl>
      </div>
    );
  }
}