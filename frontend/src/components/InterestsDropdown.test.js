import React from 'react';
import InterestsDropdown from './InterestsDropdown';
import renderer from 'react-test-renderer';
import Adapter from 'enzyme-adapter-react-16';
import {configure, shallow} from 'enzyme';

configure({adapter: new Adapter()});

describe('Interests Dropdown', () => {
  it('should be defined', () => {
    expect(InterestsDropdown).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<InterestsDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
  it('should be populated with 20 interests', () => {
    const menuItems = shallow(<InterestsDropdown />)
        .find('[data-testid="menu-option"]');
    expect(menuItems).toHaveLength(20);
  });
  it('should be populated in alphabetical order', () => {
    const menuItems = shallow(<InterestsDropdown />)
        .find('[data-testid="menu-option"]');
    const menuOptions = menuItems.map((node) => node.key());
    const sortedMenuOptions = menuOptions.sort();

    expect(menuOptions).toBe(sortedMenuOptions);
  });
});
