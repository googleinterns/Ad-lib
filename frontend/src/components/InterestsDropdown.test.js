// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import React from 'react';
import InterestsDropdown from './InterestsDropdown';
import renderer from 'react-test-renderer';
import Adapter from 'enzyme-adapter-react-16';
import {configure, shallow} from 'enzyme';

configure({adapter: new Adapter()});

let menuItems;
beforeAll(() => {
  menuItems = shallow(<InterestsDropdown />)
      .find('[data-testid="menu-option"]');
});

describe('Interests Dropdown', () => {
  it('should be defined', () => {
    expect(InterestsDropdown).toBeDefined();
  });
  it('should render correctly', () => {
    const tree = renderer.create(<InterestsDropdown />).toJSON();
    expect(tree).toMatchSnapshot();
  });
  it('should be populated with more than one interest', () => {
    expect(menuItems.length).toBeGreaterThan(0);
  });
  it('should be populated in alphabetical order', () => {
    const menuOptions = menuItems.map((node) => node.key());
    const sortedMenuOptions = menuOptions.sort();

    expect(menuOptions).toBe(sortedMenuOptions);
  });
});
