import React from 'react';
import PropTypes from 'prop-types';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';

// TO-DO: Add more product area options to this array
const productAreas = [
  'Play',
  'Search',
  'Ads',
  'Youtube',
  'Maps',
];

// Add onChange to props validation
ProductAreaDropdown.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create product area dropdown component using productAreas array
 * @return {ProductAreaDropdown} ProductAreaDropdown component
 * @param {Object} props
 */
export default function ProductAreaDropdown(props) {
  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="productArea">Product Area</InputLabel>
        <Select
          id="productArea-input"
          name="productArea"
          onChange={(event) => props.onChange(event.target.value)}
          inputProps={{'aria-label': 'productArea'}}
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
