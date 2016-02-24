function [ meanOE ] = convertOscOeToMeanOe( oscOE )
%CONVERTOSCOETOMEANOE This is a wrapper function for osc2mean that handles
%oscOE is a vector 7 elements long (a e i omega raan ta ma)

oscOE(1) = oscOE(1)*10^-3; %convert meters to km (this is what osc2mean expects)
oscOE(3) = wrapToPi(wrapToPi(oscOE(3))); %inclination should be between 0 and pi
oscOE(4) = wrapTo2Pi(wrapToPi(oscOE(4))); %omega
oscOE(5) = wrapTo2Pi(wrapToPi(oscOE(5))); %raan
oscOE(6) = wrapTo2Pi(wrapToPi(oscOE(6))); %true anomaly

meanOE = osc2mean(oscOE(1:6)); %osc2mean function from Alex

meanOE(1) = meanOE(1)*10^3;%convert back to meters; 
meanOE(7) = trueAnomToMeanAnom(meanOE(2), meanOE(6));

meanOE = meanOE';
end

