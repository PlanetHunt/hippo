function [ chiefMeanOE ] = setChiefOrbitalElements(chiefNr)

switch chiefNr
    case 1 % hin cube 4/12/2013 11:00:00
        chiefMeanOE = [7017102.334; 0.006535; 97.858*pi/180; 145.60397*pi/180; 50.48597*pi/180; 67.17897*pi/180; 67.871*pi/180];
    
    case 2 % Shaubs chief - from his paper
        
        %Chief OE from Schaubs paper
        chiefMeanOE = [7555000; 0.05; 48*pi/180; 20*pi/180; 10*pi/180; 0; 120*pi/180];
        
        %calc true anomaly as shaub didnt provide it
        %calc true anomaly
        true_anomaly = meanAnomToTrueAnom(chiefMeanOE(2), chiefMeanOE(7) );
        chiefMeanOE(6) = true_anomaly;
        

    case 3 %UWE-3 slightly shifted: %date: 2014.01.01 00:00:00.000
        a = 7019120.191+500;
        e = 0.007879+0.002;
        in =  97.873/180*pi;
        omega = 71.994/180*pi+pi/100;
        raan = 76.949/180*pi+pi/100;
        mean_anomaly = 340.151/180*pi+pi/100;
        
        chiefMeanOE = [a;e;in;omega;raan;0;mean_anomaly];

        %calc true anomaly
        true_anomaly = meanAnomToTrueAnom(chiefMeanOE(2), chiefMeanOE(7) );
        chiefMeanOE(6) = true_anomaly;
end
% chiefMeanOE=chiefMeanOE';
end