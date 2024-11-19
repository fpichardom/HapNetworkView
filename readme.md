# HapNetworkView

A java-based program for constructing and visualizing haplotype networks.

# Description

  - HapNetworkView integrates fastHaN(<https://github.com/ChenHuaLab/fastHaN>) to construct haplotype networks efficiently.
  - The use of the java library GraphStream enables better performance for visualization and auto layout of haplotype networks.
  - HapNetworkView supports datasets with large sample size (for example, N=5000)

# Launch software

  We have included all three systems in a single compressed package. Users can download and use the appropriate system as needed.

  1. For Windows: a simple double-click on "HapNetworkView_1.0_windows.bat" initiates the software

  2. For Linux: sh HapNetworkView_1.0_linux.sh

  3. For MacOS: sh HapNetworkView_1.0_mac.sh

# Quick start

  step1. Click button ***Open(.gml .phylip .vcf)*** to input gml, phylip or phased vcf files. HapNetworkView will display an uncolored haplotype network upon loading. 
  
  step2. Click button ***Group file/Map file(phylip)*** to input group information. 
  
  step3. Click button ***Save*** to save figures.

# Usage

### Adjusting the appenance of network

  We provide multiple parameters to change the apperance of network
  
  ***Hover***：Whether to display information upon mouse hovering.
  
  ***AutoLayout***：Whether to apply automatic layout optimization. When this parameter is enabled, dragging a node will also modify the overall layout. Disabling this parameter allows individual node dragging only. 
  
  ***Node***：Whether to display node ID. 
  
  ***Edge***：Whether to display the number of mutation of link. 
  
  ***Node Size***：Input the range of node size and click Run. 
  
  ***Edge size***：Input the range of edge size and click Run. 
  
  ***Edge force & Node gravity***：Adjust these two parameters and click Run to change the layout. 
  
  ***Line width***：Input the line width and click Run. 
  
  ***Node(true) & Node(intermediate)***：Tick Outline to outline node，users can also change the color of these nodes.

  We also provide users with a default color scheme and color customization.

### Check the distribution of SNP(s)

  After entering input files in phylip and phased vcf formats, users can select SNP(s) of interest in the bottom-right panel. These SNP combinations will be automatically assigned colors and displayed in the haplotype network. Please note that the number of SNP combinations should not exceed 10. ![image.png](https://cdn.nlark.com/yuque/0/2023/png/38378023/1691818457690-c05bd935-cd64-47b5-bab8-93fa6b08f71c.png#averageHue=%2523faf9f9&clientId=u7446bd05-af3f-4&from=paste&height=313&id=udccaf776&originHeight=1125&originWidth=1789&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=296041&status=done&style=none&taskId=u9c8ff786-141d-4ddf-b5cf-a0e5f8e5296&title=&width=497.3333435058594)

### Check the distribution of one gruop

  When users click on a sample or category, the corresponding haplotypes will be displayed in shadow form.
  1. click on category ![image.png](https://cdn.nlark.com/yuque/0/2023/png/38378023/1691818514217-55f37183-91b4-4b27-987d-b2ddb0cdc2d2.png#averageHue=%2523f9f8f7&clientId=u7446bd05-af3f-4&from=paste&height=245&id=ud7641975&originHeight=848&originWidth=1730&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=250278&status=done&style=none&taskId=uc0320fc8-62d3-4d0f-af6b-546c44aced4&title=&width=500.3333740234375) 
  
  2. click on sample: ![image.png](https://cdn.nlark.com/yuque/0/2023/png/38378023/1691818564768-76b7f806-dd71-4bea-aab2-0f13063eca0f.png#averageHue=%2523faf9f9&clientId=u7446bd05-af3f-4&from=paste&height=265&id=u9a2921bc&originHeight=906&originWidth=1705&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=235821&status=done&style=none&taskId=u60afd7b6-4a26-467f-8d4c-346239b0105&title=&width=498.3333740234375)

### Information of each edge and node

  After ticking Hover，hovering over a node and right-clicking, the haplotype information will be displayed on the node. Users can click button ***Save*** to save the detailed information.
  ![image.png](https://cdn.nlark.com/yuque/0/2023/png/38378023/1691820046718-e92821d1-880b-4a48-a8bd-4912bd05fe73.png#averageHue=%2523faf9f9&clientId=u7446bd05-af3f-4&from=paste&height=316&id=u19732214&originHeight=1347&originWidth=2140&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=286284&status=done&style=none&taskId=u6c8de85a-3526-48f9-8dc4-65033180868&title=&width=502.3333740234375)

  When hovering over a branch and right-clicking, the mutation information can be displayed. 
  ![image.png](https://cdn.nlark.com/yuque/0/2023/png/38378023/1691820261384-f1510755-5172-4eed-b356-6692092613f7.png#averageHue=%2523fafafa&clientId=u7446bd05-af3f-4&from=paste&height=314&id=u04f2f08f&originHeight=1341&originWidth=2137&originalType=binary&ratio=1.5&rotation=0&showTitle=false&size=252643&status=done&style=none&taskId=u3a3bbbc3-3908-4d95-ae02-c5a53c5cabb&title=&width=500.3333740234375)

# Input and Output

### Input

  genomic data 
  1. gml: only used to perform visualization 
  2. phylip and phased vcf: used to both construct and visualize haplotype networks group file has two columns corresponding to sample ID and sample group, the file supports different delimiters such as tab, space, comma, and semicolon.

### Output

  png, pdf, svg
