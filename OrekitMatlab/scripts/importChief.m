function [ ] = importChief( )
global chiefTimeVectorNum oecm;
% timeFilePath = fullfile(pwd, 'dataFiles', 'chiefTimeVector4Orbits120sMaxAdpativeStep.mat');
% dataFilePath = fullfile(pwd, 'dataFiles', 'oecm4Orbits120sMaxAdpativeStep.mat');
% timeFilePath = fullfile(pwd, 'dataFiles', 'schaubTIME2m26d14h44mStep240s.mat');
% dataFilePath = fullfile(pwd, 'dataFiles', 'schaubOECM2m26d14h44mStep240s.mat');

% timeFilePath = fullfile(pwd, 'dataFiles', 'Time4Orbits240sHinCube.mat');
% dataFilePath = fullfile(pwd, 'dataFiles', 'oecm4Orbits240sHinCube.mat');

% timeFilePath = fullfile(pwd, 'dataFiles', 'timeHINCUBE4days240sStep.mat');
% dataFilePath = fullfile(pwd, 'dataFiles', 'oecmHINCUBE4days240sStep.mat');

% timeFilePath = fullfile(pwd, 'dataFiles', 'timeVector_Hin_oe_20days_120smaxStep.mat');
% dataFilePath = fullfile(pwd, 'dataFiles', 'oecm_Hin20days_120smaxStep.mat');

% timeFilePath = fullfile(pwd, 'dataFiles', 'timeVec_schaub_7days_120sStep.mat');
% dataFilePath = fullfile(pwd, 'dataFiles', 'oecm_schaub_7days_120sStep.mat');

timeFilePath = fullfile(pwd, 'dataFiles', 'time__schaub_7days_NewFixed_120s.mat');
dataFilePath = fullfile(pwd, 'dataFiles', 'oecm__schaub_7days_NewFixed_120s.mat');



load(timeFilePath);
load(dataFilePath);
chiefTimeVectorNum = datenum(timeVector);
oecm = oecm;
timeVector = datetime(0001,01,01,000,00,00);
end

