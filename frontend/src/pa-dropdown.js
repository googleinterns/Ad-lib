import React from 'react';
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

export default function ProductAreaDropdown() {
  const [productArea, setProductArea] = React.useState([]);

  const handleChange = (event) => {
    setProductArea(event.target.value);
  };

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="productArea">Product Area</InputLabel>
        <Select
          id="productArea-input"
          name="productArea"
          value={productArea}
          onChange={handleChange}
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
