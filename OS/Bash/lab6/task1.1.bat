ver > ver.txt
chcp 1251
systeminfo | find "��������� ���������� ������" > 1_1_systeminfo.txt
systeminfo | find "������ ����� ���������� ������" >> 1_1_systeminfo.txt
wmic LOGICALDISK GET DeviceId, VolumeName, Description, Size > 1_1_wmic.txt
pause