import React from 'react';
import RoleDropdown from './RoleDropdown';
import renderer from 'react-test-renderer';

describe('Role Dropdown', () => {
  it('should be defined', () => {
    expect(RoleDropdown).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<RoleDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});
