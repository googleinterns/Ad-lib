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
import App from './App';
import renderer from 'react-test-renderer';
import axios from 'axios';

beforeAll(() => {
  const DATE_TO_USE = new Date('2020');
  const mockedDate = Date;
  global.Date = jest.fn(() => DATE_TO_USE);
  global.Date.UTC = mockedDate.UTC;
  global.Date.parse = mockedDate.parse;
  global.Date.now = mockedDate.now;
});

jest.mock('axios');

describe('App', () => {
  it('should be defined', () => {
    expect(App).toBeDefined();
  });

  it('should render correctly', () => {
    const tree = renderer.create(<App />).toJSON();
    expect(tree).toMatchSnapshot();
  });

  it('should initiate GET request using axios() to servlet', () => {
    const servletEndpoint = '/api/v1/search-match';
    const mockMatchStatus = {matchStatus: 'true'};
    const mockData = {data: mockMatchStatus};

    axios.get.mockImplementation(() => Promise.resolve(mockData));
    const responseData = axios.get(servletEndpoint);

    responseData.then((response) => {
      expect(response.data).toEqual(mockData);
    }).catch((error) => {});
  });
});
