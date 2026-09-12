// SPDX-License-Identifier: MIT
pragma solidity ^0.8.24;

/**
 * ============================================================================
 *               STACKNSCALE AI STUDIOS — COMPREHENSIVE ON-CHAIN PROTOCOL
 * ============================================================================
 * 
 * Architecture: ScalenStack + Dime10 + 4Real.io + V€€³ OG8OH OS
 * Ecosystem: SaaS, HaaS (Hardware-as-a-Service), and Fractional IP Vault
 * Target Network: Base L2 (Optimized for Basescan.org & EVM Mainnet)
 * 
 * ----------------------------------------------------------------------------
 * LEGAL PROVENANCE & INTELLECTUAL PROPERTY NOTICE:
 * ----------------------------------------------------------------------------
 * Created by: Dexter Lamar Vann (Author, Principal Architect & Sole IP Owner)
 * Legal Entity: Vann Family Ventures LLC
 * Creative Muse: Samantha J. Campensa (Founder & Visionary of StacknScale Studios)
 * GitHub Profile: https://github.com/dextervann1
 * Block Explorer: https://basescan.org
 * All Rights Reserved © 2026 Vann Family Ventures LLC
 * ============================================================================
 */

interface IERC165 {
    function supportsInterface(bytes4 interfaceId) external view returns (bool);
}

interface IERC721 is IERC165 {
    event Transfer(address indexed from, address indexed to, uint256 indexed tokenId);
    event Approval(address indexed owner, address indexed approved, uint256 indexed tokenId);
    event ApprovalForAll(address indexed owner, address indexed operator, bool approved);

    function balanceOf(address owner) external view returns (uint256 balance);
    function ownerOf(uint256 tokenId) external view returns (address owner);
    function safeTransferFrom(address from, address to, uint256 tokenId, bytes calldata data) external;
    function safeTransferFrom(address from, address to, uint256 tokenId) external;
    function transferFrom(address from, address to, uint256 tokenId) external;
    function approve(address to, uint256 tokenId) external;
    function setApprovalForAll(address operator, bool approved) external;
    function getApproved(uint256 tokenId) external view returns (address operator);
    function isApprovedForAll(address owner, address operator) external view returns (bool);
}

interface IERC2981 is IERC165 {
    function royaltyInfo(uint256 tokenId, uint256 salePrice) external view returns (address receiver, uint256 royaltyAmount);
}

interface IZkGroth16Verifier {
    function verifyProof(
        uint256[2] calldata a,
        uint256[2][2] calldata b,
        uint256[2] calldata c,
        uint256[4] calldata input
    ) external view returns (bool r);
}

abstract contract Context {
    function _msgSender() internal view virtual returns (address) {
        return msg.sender;
    }

    function _msgData() internal view virtual returns (bytes calldata) {
        return msg.data;
    }
}

abstract contract Ownable is Context {
    address private _owner;

    event OwnershipTransferred(address indexed previousOwner, address indexed newOwner);

    constructor(address initialOwner) {
        require(initialOwner != address(0), "Ownable: zero address");
        _owner = initialOwner;
        emit OwnershipTransferred(address(0), initialOwner);
    }

    modifier onlyOwner() {
        _checkOwner();
        _;
    }

    function owner() public view virtual returns (address) {
        return _owner;
    }

    function _checkOwner() internal view virtual {
        require(owner() == _msgSender(), "Ownable: caller is not the owner");
    }

    function transferOwnership(address newOwner) public virtual onlyOwner {
        require(newOwner != address(0), "Ownable: new owner is the zero address");
        _transferOwnership(newOwner);
    }

    function _transferOwnership(address newOwner) internal virtual {
        address oldOwner = _owner;
        _owner = newOwner;
        emit OwnershipTransferred(oldOwner, newOwner);
    }
}

abstract contract ReentrancyGuard {
    uint256 private constant _NOT_ENTERED = 1;
    uint256 private constant _ENTERED = 2;
    uint256 private _status;

    constructor() {
        _status = _NOT_ENTERED;
    }

    modifier nonReentrant() {
        require(_status != _ENTERED, "ReentrancyGuard: reentrant call");
        _status = _ENTERED;
        _;
        _status = _NOT_ENTERED;
    }
}

/**
 * @title StacknScaleMasterProtocol
 * @dev Main orchestration contract unifying Dime10 IP tokenization, 4Real.io HaaS sensory stems,
 *      ScalenStack ZK proof verification, and SaaS licensing for Vann Family Ventures LLC.
 */
contract StacknScaleMasterProtocol is Context, Ownable, ReentrancyGuard, IERC721, IERC2981 {

    // =========================================================================
    // CONSTANTS & BRAND METADATA
    // =========================================================================
    string public constant APP_NAME = "StacknScale ai studios";
    string public constant CREATOR = "Dexter Lamar Vann";
    string public constant COMPANY = "Vann Family Ventures LLC";
    string public constant CREATIVE_MUSE = "Samantha J. Campensa";
    string public constant GITHUB_URL = "https://github.com/dextervann1";
    string public constant BASE_EXPLORER = "https://basescan.org";
    string public constant SYMBOL = "SnS-VFV";

    // Totem identifiers
    enum TotemType { DIME10_RED, SENSORY_GREEN, SCALENSTACK_CYAN, VEE3_PURPLE }

    // =========================================================================
    // DIME10 INTELLECTUAL PROPERTY & TOKEN-BOUND STRUCTS
    // =========================================================================
    struct Dime10IPAsset {
        uint256 tokenId;
        string ipfsHash;
        string assetName;
        TotemType totem;
        uint96 royaltyBasisPoints; // e.g. 500 = 5.00%
        uint256 totalShares;
        uint256 pricePerShare;
        address creator;
        address tokenBoundAccount; // ERC-6551 Registry TBA
        bool activeForSale;
    }

    // =========================================================================
    // 4REAL.IO SENSORY HAAS (HARDWARE-AS-A-SERVICE) TELEMETRY STRUCTS
    // =========================================================================
    struct HaaSSensoryNode {
        bytes32 nodeHardwareId;
        address operator;
        string locationTag;
        uint32 hapticFrequencyHz;
        uint16 decibelLevel;
        uint32 opticalLux;
        uint64 lastPingTimestamp;
        bool isOnline;
        uint256 totalTelemetrySubmissions;
    }

    // =========================================================================
    // SAAS SUBSCRIPTION & MCP (MODEL CONTEXT PROTOCOL) STRUCTS
    // =========================================================================
    enum SaaSTier { FREE, DEVELOPER, ENTERPRISE_PRO, VFV_FOUNDER }

    struct SubscriptionPlan {
        SaaSTier tier;
        uint256 monthlyFeeWei;
        uint32 maxMcpCallsPerDay;
        uint32 maxZkVerificationsPerDay;
        bool hasHaaSStreamAccess;
    }

    struct UserSubscription {
        SaaSTier tier;
        uint256 validUntil;
        uint32 mcpCallsUsedToday;
        uint64 lastCallDayIndex;
    }

    struct McpToolRecord {
        bytes32 toolHash;
        string toolName;
        string mcpServerEndpoint;
        address provider;
        uint256 executionCount;
        bool verified;
    }

    // =========================================================================
    // STORAGE STATE
    // =========================================================================
    uint256 private _nextTokenId = 1001;
    
    // ERC-721 Storage
    mapping(uint256 => address) private _owners;
    mapping(address => uint256) private _balances;
    mapping(uint256 => address) private _tokenApprovals;
    mapping(address => mapping(address => bool)) private _operatorApprovals;

    // Dime10 Assets
    mapping(uint256 => Dime10IPAsset) public ipAssets;
    mapping(uint256 => mapping(address => uint256)) public fractionalShares; // tokenId => (user => shareCount)

    // 4Real.io HaaS Nodes
    mapping(bytes32 => HaaSSensoryNode) public sensoryNodes;
    bytes32[] public registeredNodeList;

    // SaaS Subscriptions
    mapping(SaaSTier => SubscriptionPlan) public tierConfigs;
    mapping(address => UserSubscription) public userSubscriptions;

    // V€€³ OG8OH MCP Registry
    mapping(bytes32 => McpToolRecord) public mcpTools;
    bytes32[] public mcpToolList;

    // ScalenStack ZK Verifier
    address public zkGroth16VerifierAddress;
    mapping(bytes32 => bool) public verifiedZkProofRoots;

    // Royalty receiver
    address public royaltyVaultReceiver;

    // =========================================================================
    // EVENTS
    // =========================================================================
    event Dime10IPMinted(uint256 indexed tokenId, string assetName, address indexed creator, TotemType totem, uint96 royaltyBps);
    event FractionalSharesPurchased(uint256 indexed tokenId, address indexed buyer, uint256 sharesPurchased, uint256 cost);
    event HaaSNodeRegistered(bytes32 indexed nodeId, address indexed operator, string locationTag);
    event HaaSTelemetryPushed(bytes32 indexed nodeId, uint32 freqHz, uint16 decibels, uint32 lux, uint64 timestamp);
    event SaaSSubscribed(address indexed subscriber, SaaSTier tier, uint256 expiration);
    event McpToolDispatched(bytes32 indexed toolHash, string toolName, address indexed caller, uint256 executionCount);
    event ZkProofSettledOnChain(bytes32 indexed proofHash, address indexed submitter, uint256[4] publicSignals);

    // =========================================================================
    // CONSTRUCTOR & INITIALIZATION
    // =========================================================================
    constructor(
        address initialOwner,
        address _zkVerifier,
        address _royaltyReceiver
    ) Ownable(initialOwner) {
        zkGroth16VerifierAddress = _zkVerifier;
        royaltyVaultReceiver = _royaltyReceiver != address(0) ? _royaltyReceiver : initialOwner;

        // Configure SaaS tiers
        tierConfigs[SaaSTier.FREE] = SubscriptionPlan(SaaSTier.FREE, 0 ether, 50, 10, false);
        tierConfigs[SaaSTier.DEVELOPER] = SubscriptionPlan(SaaSTier.DEVELOPER, 0.015 ether, 2000, 500, true);
        tierConfigs[SaaSTier.ENTERPRISE_PRO] = SubscriptionPlan(SaaSTier.ENTERPRISE_PRO, 0.08 ether, 50000, 10000, true);
        tierConfigs[SaaSTier.VFV_FOUNDER] = SubscriptionPlan(SaaSTier.VFV_FOUNDER, 0.25 ether, 1000000, 1000000, true);
    }

    // =========================================================================
    // DIME10 IP & FRACTIONAL TOKENIZATION FUNCTIONS
    // =========================================================================
    /**
     * @notice Mints a new Fractional Intellectual Property Asset Token
     */
    function mintDime10IP(
        string calldata assetName,
        string calldata ipfsHash,
        TotemType totem,
        uint96 royaltyBps,
        uint256 totalShares,
        uint256 pricePerShareWei,
        address tokenBoundAccount
    ) external nonReentrant returns (uint256) {
        require(bytes(assetName).length > 0, "Asset name required");
        require(royaltyBps <= 2500, "Max royalty 25%");
        require(totalShares > 0, "Shares must be > 0");

        uint256 tokenId = _nextTokenId++;
        address sender = _msgSender();

        _owners[tokenId] = sender;
        _balances[sender] += 1;

        ipAssets[tokenId] = Dime10IPAsset({
            tokenId: tokenId,
            ipfsHash: ipfsHash,
            assetName: assetName,
            totem: totem,
            royaltyBasisPoints: royaltyBps,
            totalShares: totalShares,
            pricePerShare: pricePerShareWei,
            creator: sender,
            tokenBoundAccount: tokenBoundAccount,
            activeForSale: true
        });

        // Assign initial 100% of shares to the creator
        fractionalShares[tokenId][sender] = totalShares;

        emit Transfer(address(0), sender, tokenId);
        emit Dime10IPMinted(tokenId, assetName, sender, totem, royaltyBps);

        return tokenId;
    }

    /**
     * @notice Purchase fractional shares of a Dime10 IP Asset
     */
    function buyFractionalShares(uint256 tokenId, uint256 shareAmount) external payable nonReentrant {
        Dime10IPAsset storage asset = ipAssets[tokenId];
        require(asset.activeForSale, "Asset not for sale");
        require(shareAmount > 0, "Share amount > 0");

        uint256 totalCost = asset.pricePerShare * shareAmount;
        require(msg.value >= totalCost, "Insufficient payment");

        address currentOwner = _owners[tokenId];
        require(fractionalShares[tokenId][currentOwner] >= shareAmount, "Not enough shares available from owner");

        fractionalShares[tokenId][currentOwner] -= shareAmount;
        fractionalShares[tokenId][_msgSender()] += shareAmount;

        // Pay creator & royalty
        uint256 royalty = (totalCost * asset.royaltyBasisPoints) / 10000;
        uint256 sellerProceeds = totalCost - royalty;

        if (royalty > 0) {
            (bool successR, ) = payable(royaltyVaultReceiver).call{value: royalty}("");
            require(successR, "Royalty transfer failed");
        }

        (bool successP, ) = payable(currentOwner).call{value: sellerProceeds}("");
        require(successP, "Proceeds transfer failed");

        // Refund excess ETH
        if (msg.value > totalCost) {
            payable(_msgSender()).transfer(msg.value - totalCost);
        }

        emit FractionalSharesPurchased(tokenId, _msgSender(), shareAmount, totalCost);
    }

    // =========================================================================
    // 4REAL.IO SENSORY HAAS (HARDWARE-AS-A-SERVICE) TELEMETRY
    // =========================================================================
    function registerHaaSNode(
        bytes32 nodeHardwareId,
        string calldata locationTag
    ) external {
        require(sensoryNodes[nodeHardwareId].operator == address(0), "Node already registered");

        sensoryNodes[nodeHardwareId] = HaaSSensoryNode({
            nodeHardwareId: nodeHardwareId,
            operator: _msgSender(),
            locationTag: locationTag,
            hapticFrequencyHz: 440,
            decibelLevel: 60,
            opticalLux: 350,
            lastPingTimestamp: uint64(block.timestamp),
            isOnline: true,
            totalTelemetrySubmissions: 0
        });

        registeredNodeList.push(nodeHardwareId);
        emit HaaSNodeRegistered(nodeHardwareId, _msgSender(), locationTag);
    }

    function pushSensoryTelemetry(
        bytes32 nodeHardwareId,
        uint32 hapticFreqHz,
        uint16 decibels,
        uint32 lux
    ) external {
        HaaSSensoryNode storage node = sensoryNodes[nodeHardwareId];
        require(node.operator == _msgSender() || owner() == _msgSender(), "Unauthorized HaaS node operator");

        node.hapticFrequencyHz = hapticFreqHz;
        node.decibelLevel = decibels;
        node.opticalLux = lux;
        node.lastPingTimestamp = uint64(block.timestamp);
        node.isOnline = true;
        node.totalTelemetrySubmissions += 1;

        emit HaaSTelemetryPushed(nodeHardwareId, hapticFreqHz, decibels, lux, uint64(block.timestamp));
    }

    // =========================================================================
    // SAAS SUBSCRIPTIONS & V€€³ OG8OH MCP TOOL REGISTRY
    // =========================================================================
    function subscribeSaaS(SaaSTier tier, uint256 months) external payable nonReentrant {
        require(tier != SaaSTier.FREE, "Free tier does not require payment");
        require(months >= 1 && months <= 24, "Invalid duration");

        SubscriptionPlan memory plan = tierConfigs[tier];
        uint256 totalCost = plan.monthlyFeeWei * months;
        require(msg.value >= totalCost, "Insufficient subscription fee");

        UserSubscription storage sub = userSubscriptions[_msgSender()];
        uint256 currentExpiry = sub.validUntil > block.timestamp ? sub.validUntil : block.timestamp;
        sub.validUntil = currentExpiry + (months * 30 days);
        sub.tier = tier;

        // Forward subscription revenue to royalty vault
        (bool success, ) = payable(royaltyVaultReceiver).call{value: totalCost}("");
        require(success, "Payment forwarding failed");

        if (msg.value > totalCost) {
            payable(_msgSender()).transfer(msg.value - totalCost);
        }

        emit SaaSSubscribed(_msgSender(), tier, sub.validUntil);
    }

    function registerMcpTool(
        string calldata toolName,
        string calldata mcpEndpoint
    ) external returns (bytes32) {
        bytes32 toolHash = keccak256(abi.encodePacked(toolName, mcpEndpoint, _msgSender(), block.timestamp));

        mcpTools[toolHash] = McpToolRecord({
            toolHash: toolHash,
            toolName: toolName,
            mcpServerEndpoint: mcpEndpoint,
            provider: _msgSender(),
            executionCount: 0,
            verified: true
        });

        mcpToolList.push(toolHash);
        return toolHash;
    }

    function executeMcpTool(bytes32 toolHash) external {
        McpToolRecord storage tool = mcpTools[toolHash];
        require(tool.verified, "Tool unverified or non-existent");

        // Rate limit check
        uint64 today = uint64(block.timestamp / 1 days);
        UserSubscription storage sub = userSubscriptions[_msgSender()];
        if (sub.lastCallDayIndex != today) {
            sub.lastCallDayIndex = today;
            sub.mcpCallsUsedToday = 0;
        }

        SaaSTier effectiveTier = (sub.validUntil > block.timestamp) ? sub.tier : SaaSTier.FREE;
        SubscriptionPlan memory plan = tierConfigs[effectiveTier];
        require(sub.mcpCallsUsedToday < plan.maxMcpCallsPerDay, "Daily MCP limit reached for plan");

        sub.mcpCallsUsedToday += 1;
        tool.executionCount += 1;

        emit McpToolDispatched(toolHash, tool.toolName, _msgSender(), tool.executionCount);
    }

    // =========================================================================
    // SCALENSTACK ZERO-KNOWLEDGE (ZK) PROOF DISPATCH & VERIFICATION
    // =========================================================================
    function verifyAndRecordZkProof(
        uint256[2] calldata a,
        uint256[2][2] calldata b,
        uint256[2] calldata c,
        uint256[4] calldata publicSignals
    ) external returns (bool) {
        bool isValid = true;
        if (zkGroth16VerifierAddress != address(0)) {
            isValid = IZkGroth16Verifier(zkGroth16VerifierAddress).verifyProof(a, b, c, publicSignals);
        }
        require(isValid, "Invalid ZK SNARK proof");

        bytes32 proofHash = keccak256(abi.encodePacked(a, b, c, publicSignals, _msgSender()));
        verifiedZkProofRoots[proofHash] = true;

        emit ZkProofSettledOnChain(proofHash, _msgSender(), publicSignals);
        return true;
    }

    // =========================================================================
    // IERC2981 & ERC721 IMPLEMENTATIONS
    // =========================================================================
    function royaltyInfo(uint256 tokenId, uint256 salePrice) external view override returns (address receiver, uint256 royaltyAmount) {
        Dime10IPAsset memory asset = ipAssets[tokenId];
        receiver = royaltyVaultReceiver;
        royaltyAmount = (salePrice * asset.royaltyBasisPoints) / 10000;
    }

    function balanceOf(address owner) external view override returns (uint256) {
        require(owner != address(0), "Zero address query");
        return _balances[owner];
    }

    function ownerOf(uint256 tokenId) public view override returns (address) {
        address owner = _owners[tokenId];
        require(owner != address(0), "Token does not exist");
        return owner;
    }

    function approve(address to, uint256 tokenId) external override {
        address owner = ownerOf(tokenId);
        require(to != owner, "Approval to current owner");
        require(_msgSender() == owner || isApprovedForAll(owner, _msgSender()), "Not authorized");
        _tokenApprovals[tokenId] = to;
        emit Approval(owner, to, tokenId);
    }

    function getApproved(uint256 tokenId) public view override returns (address) {
        require(_owners[tokenId] != address(0), "Token does not exist");
        return _tokenApprovals[tokenId];
    }

    function setApprovalForAll(address operator, bool approved) external override {
        require(operator != _msgSender(), "Approve to caller");
        _operatorApprovals[_msgSender()][operator] = approved;
        emit ApprovalForAll(_msgSender(), operator, approved);
    }

    function isApprovedForAll(address owner, address operator) public view override returns (bool) {
        return _operatorApprovals[owner][operator];
    }

    function transferFrom(address from, address to, uint256 tokenId) public override nonReentrant {
        require(_isApprovedOrOwner(_msgSender(), tokenId), "Caller is not owner nor approved");
        _transfer(from, to, tokenId);
    }

    function safeTransferFrom(address from, address to, uint256 tokenId) external override {
        safeTransferFrom(from, to, tokenId, "");
    }

    function safeTransferFrom(address from, address to, uint256 tokenId, bytes calldata data) public override {
        transferFrom(from, to, tokenId);
    }

    function _transfer(address from, address to, uint256 tokenId) internal {
        require(ownerOf(tokenId) == from, "Transfer from incorrect owner");
        require(to != address(0), "Transfer to zero address");

        delete _tokenApprovals[tokenId];
        _balances[from] -= 1;
        _balances[to] += 1;
        _owners[tokenId] = to;

        emit Transfer(from, to, tokenId);
    }

    function _isApprovedOrOwner(address spender, uint256 tokenId) internal view returns (bool) {
        address owner = ownerOf(tokenId);
        return (spender == owner || isApprovedForAll(owner, spender) || getApproved(tokenId) == spender);
    }

    function supportsInterface(bytes4 interfaceId) external pure override returns (bool) {
        return interfaceId == type(IERC721).interfaceId ||
               interfaceId == type(IERC2981).interfaceId ||
               interfaceId == type(IERC165).interfaceId;
    }

    // =========================================================================
    // ADMIN CONFIGURATION
    // =========================================================================
    function setZkVerifier(address _verifier) external onlyOwner {
        zkGroth16VerifierAddress = _verifier;
    }

    function setRoyaltyReceiver(address _receiver) external onlyOwner {
        require(_receiver != address(0), "Invalid receiver");
        royaltyVaultReceiver = _receiver;
    }

    function updateTierPlan(
        SaaSTier tier,
        uint256 monthlyFeeWei,
        uint32 maxMcpCalls,
        uint32 maxZkCalls,
        bool haasStream
    ) external onlyOwner {
        tierConfigs[tier] = SubscriptionPlan(tier, monthlyFeeWei, maxMcpCalls, maxZkCalls, haasStream);
    }
}
