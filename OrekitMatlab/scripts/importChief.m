function [ ] = importChief( )
global chiefTimeVectorNum oecm;
timeFilePath = fullfile(pwd, 'dataFiles', 'chiefTimeVector4Orbits120sMaxAdpativeStep.mat');
dataFilePath = fullfile(pwd, 'dataFiles', 'oecm4Orbits120sMaxAdpativeStep.mat');
load(timeFilePath);
load(dataFilePath);
chiefTimeVectorNum = datenum(timeVector);
oecm = oecm;
timeVector = datetime(0001,01,01,000,00,00);
end

