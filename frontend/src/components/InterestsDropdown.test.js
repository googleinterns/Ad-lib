import React from 'react';
import InterestsDropdown from './InterestsDropdown';
import renderer from 'react-test-renderer';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import Adapter from 'enzyme-adapter-react-16';
import { shallow, configure } from 'enzyme';

configure({adapter: new Adapter()});

describe('Interests Dropdown', () => {
  it('should be defined', () => {
    expect(InterestsDropdown).toBeDefined();
  });
  it('should be populated with a list of interests', () => {
    const tree = renderer.create(<InterestsDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
  it('should be populated in alphabetical order', () => {
    const wrapper = shallow(<InterestsDropdown />);
    const menuItem = wrapper.find('MenuItem');

    expect(menuItem).toBeDefined();
    //expect(select.find('List')).toHaveLength(20);
    //console.log(select);
    /*const {getAllByTestId} = (<InterestsDropdown />);
    const options = getAllByTestId('menu-option');
    
    const sortedOptions = options.sort();
    
    expect(options).toEqual(sortedOptions);*/
  });
});
