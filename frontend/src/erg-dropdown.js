import React from 'react';
import Input from '@material-ui/core/Input';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import ListItemText from '@material-ui/core/ListItemText';
import Select from '@material-ui/core/Select';
import Checkbox from '@material-ui/core/Checkbox';

const ergs = [
  'Africans@',
  'AGN',
  'BGN',
  'Disability Alliance',
  'FGN',
  'GAIN',
  'Gayglers',
  'Greyglers',
  'HOLA',
  'IBN',
  'IGN',
  'Iranian Googlers',
  'Mosaic',
  'Trans@',
  'VetNet',
  'Women@'
];

export default function ERGDropdown() {
  const [erg, setErg] = React.useState([]);

  const handleChange = (event) => {
    setErg(event.target.value);
  };

  return (
    <div>
      <FormControl style={{width: 180}}>
        <InputLabel id="erg">ERG Affiliation</InputLabel>
        <Select
          id="erg"
          multiple
          value={erg}
          onChange={handleChange}
          input={<Input />}
          renderValue={(selected) => selected.join(', ')}
          inputProps={{ "aria-label": "erg" }}
        >
          {ergs.map((currentErg) => (
            <MenuItem key={currentErg} value={currentErg}>
              <Checkbox checked={erg.includes(currentErg)} />
              <ListItemText primary={currentErg} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
}
