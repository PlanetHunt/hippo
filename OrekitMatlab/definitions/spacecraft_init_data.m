function [a,e,in,omega,raan,mean_anomaly,date,step_size,duration] = spacecraft_init_data(mu,i)


switch i
     case 1 %CanX-4:
        % date: 2014.07.04 16:55:41.240
        n = 14.73345459;
        a = (mu/(n*2*pi/(24*3600))^2)^(1/3);
        e = 0.0012952;
        in = 98.2557/180*pi;
        omega = 258.543/180*pi;
        raan = 252.93/180*pi;
        mean_anomaly = 101.433;%/180*pi;
    case 2 %CanX-5:
        % date: 2014.07.04 18:32:30.036
        n = 14.73568733;
        a = (mu/(n*2*pi/(24*3600))^2)^(1/3);
        e = 0.001037;
        in = 98.2534/180*pi;
        omega = 255.869/180*pi;
        raan = 252.995/180*pi;
        mean_anomaly = 104.137;%/180*pi;
    case 3 %UWE-3: %date: 2014.01.01 00:00:00.000
        a = 7019120.191;
        e = 0.007879;
        in =  97.873/180*pi;
        omega = 71.994/180*pi;
        raan = 76.949/180*pi;
        mean_anomaly = 340.151/180*pi;
end
%% Simulator settings:
date = [2014, 01, 01, 00, 00, 00.000];

% date = [2014,07,04,16,50,32.000];
step_size = 60;
duration = 96*60*1;