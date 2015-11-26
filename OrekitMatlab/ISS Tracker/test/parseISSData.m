%%
% Get and parse ISS Data, same as in the ISSTracker.m
% Author : Rami Houdroge
% Version : 1.0.0
% Created : 2011
% Revision : $Id: parseISSData.m 34 2013-07-17 21:12:55Z Rami $
%%
function [data, header] = parseISSData ()
%PARSEISSDATA Retrive and store ISS Data
%   Input : Nasa SVPOST.html
%   Output : Matlab struc with OREKIT elements


nasaData = getISSData();
% cd('data')
% nasaData = open('nasaData.mat');
% nasaData = nasaData.nasaData;
% cd('..')

[header, cOe] = splitOe( nasaData );

data = parseOe(cOe);


end

function [ge, oeSplit] = splitOe( bigChar )

% Split into strings for each coasting arc
str = '    Coasting Arc #';
idx = [strfind(bigChar, str), length(bigChar)];

oeSplit = {};
for k = 1 : length(idx) - 1
    oeSplit{length(oeSplit)+1} = bigChar(idx(k):idx(k+1));
end

header = bigChar(1:idx(1)-1);
headerData = regexp(header,'-?\d+.\d+','match');

ge.area = str2double(headerData{1});
ge.dragCoefficient = str2double(headerData{2});
ge.solarFlux = str2double(headerData{4});

end

function sOe = parseOe( cOe )


labels = {'X', 'Y', 'Z', 'XDot', 'YDot', 'ZDot'};

for k = 1:length(cOe)
    
    tcOe = cOe{k};
    
    % parse into lines
    tsOe.text.raw = tcOe;
    tsOe.text.lines = regexp(tcOe, '\n', 'split');
    
    % get time info
    tsOe.time.text = tsOe.text.lines{4};
    dateData = char(regexpi(tsOe.time.text,...
        '\d+/\d+/\d\d:\d\d:\d\d.\d\d\d','match'));
    tsOe.time.year = str2double(dateData(1:4));
    tsOe.time.doy = str2double(dateData([6 7 8]));
    tsOe.time.hour = str2double(dateData([10 11]));
    tsOe.time.minute = str2double(dateData([13 14])); %#ok<*AGROW>
    tsOe.time.second = str2double(dateData([16 17]));
    tsOe.time.msecond = str2double(dateData([19 20 21]));
    
    % tsOe.time.date = AbsoluteDate(tsOe.time.year, ...
    %     tsOe.time.month, tsOe.time.day, ...
    %     tsOe.time.hour, tsOe.time.minute, ...
    %     tsOe.time.second + tsOe.time.msecond/1000, ...
    %     utc);
    
    % get cartesian info
    for j = 22:27
        result = regexp(tsOe.text.lines{j},'(-?[0-9]{0,}.\d+)*','match');
        tsOe.data.(labels{j-21}) = str2double(result{2});
    end
    
    tsOe.data.weight = str2double( regexpi(tsOe.text.lines{6}, '\d+.\d+', 'match'));
    
     sOe(k) = tsOe;
end

end

function nasaData = getISSData( input_args )
%GETISSDATA Summary of this function goes here
%   Get the ISS data

% Default Nasa feed
url = 'http://spaceflight.nasa.gov/realdata/sightings/SSapplications/Post/JavaSSOP/orbit/ISS/SVPOST.html';

% test input
if nargin == 1 && input_args{0} ~= ''
    url = userUrl;
end

try
    % Get data
    nasaData = urlread(url);
    
    % Split the nasaData char starting from the startStr
    str = '<B> ISS  TRAJECTORY DATA</B>';
    idx(1) = strfind(nasaData, str) + length(str);
    str = '</PRE>';
    idx(2) = strfind(nasaData, str) - 1;
    
    % Return main content
    nasaData = nasaData(idx(1):idx(2));
    
catch e
    error('Couldn''t retrieve orbital data');
end

end

